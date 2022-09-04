package com.app.lockapp4.framework.database

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.lockapp4.*
import com.app.lockapp4.framework.utl.commonTranslateCalendarToStringYYYYMMDDHHMM
import com.app.lockapp4.framework.utl.commonTranslateLongToCalendar
import com.app.lockapp4.framework.utl.constEmergencyTapNumberRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    lockTimeDao: LockTimeDao,
    instantLockDao: InstantLockDao,
    nextOrDuringLockTimeDao: NextOrDuringLockTimeDao,
    statusOnScreenDao: StatusOnScreenDao
) : ViewModel() {

    var lockTimes = lockTimeDao.getAllFlow()
    var instantLock = instantLockDao.getAllFlow()
    var nextOrDuringLockTime = nextOrDuringLockTimeDao.getAllFlow()

    var isEmergencyUnlocked by mutableStateOf(false)
    var emergencyTapNumber by mutableStateOf(0)
    var instantLockTimeOnScreenMinuteInInt by mutableStateOf(1)

    private var lockTimeDaoForUse = lockTimeDao
    private var instantLockDaoForUse = instantLockDao
    private var nextOrDuringLockTimeDaoForUse = nextOrDuringLockTimeDao
    private var statusOnScreenDaoForUse = statusOnScreenDao

    fun tapEmergencyButton(context: Context) {
        Timber.d("■■■■■■■■■■■tapEmergencyButton開始")
        emergencyTapNumber += 1
        if (emergencyTapNumber == constEmergencyTapNumberRequired) {
            deleteInstantLock(context)
            isEmergencyUnlocked = true
            emergencyTapNumber = 0
            cancelScheduleForDuringForeground(context)
        }
        Timber.d("■■■■■■■■■■■tapEmergencyButton終了")
    }

    fun resetStatusOnScreenOnDB(){

    }

    fun insertInstantLock(context: Context) {
        Timber.d("■■■■■■■■■■■insertInstantLock開始")
        isEmergencyUnlocked = false
        val now = Calendar.getInstance()

        Timber.d("■■■■■■■■■■■now:" + commonTranslateCalendarToStringYYYYMMDDHHMM(now))

        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.insert(
                InstantLock(
                    0,
                    now.timeInMillis,
                    instantLockTimeOnScreenMinuteInInt.toLong()
                )
            )
            scheduleForDuringForeground(context)
        }
        Timber.d("■■■■■■■■■■■insertInstantLock終了")
    }

    fun deleteInstantLock(context: Context) {
        Timber.d("■■■■■■■■■■■deleteInstantLock開始")
        isEmergencyUnlocked = false
        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.deleteAll()
            scheduleForDuringForeground(context)

            Timber.d("■■■■■■■■■■■deleteInstantLock終了")
        }
    }

    fun updateToTime(context: Context,dayId: Int, mHour: Int, mMinute: Int) {
        Timber.d("■■■■■■■■■■■updateToTime開始")
        isEmergencyUnlocked = false
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateToTime(dayId, mHour, mMinute)
            recalculateNextOrDuringLockTimeInsideViewModel(context)
            scheduleForDuringForeground(context)
            Timber.d("■■■■■■■■■■■updateToTime終了")
        }
    }

    fun updateFromTime(context: Context,dayId: Int, mHour: Int, mMinute: Int) {
        Timber.d("■■■■■■■■■■■updateFromTime開始")
        isEmergencyUnlocked = false
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateFromTime(dayId, mHour, mMinute)
            recalculateNextOrDuringLockTimeInsideViewModel(context)
            scheduleForDuringForeground(context)
            Timber.d("■■■■■■■■■■■updateFromTime終了")
        }
    }

    fun updateEnable(context: Context,lockTime: LockTime): Boolean {
        Timber.d("■■■■■■■■■■■updateEnable開始")
        isEmergencyUnlocked = false
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateEnable(lockTime)
            recalculateNextOrDuringLockTimeInsideViewModel(context)
            scheduleForDuringForeground(context)
            Timber.d("■■■■■■■■■■■updateEnable終了")
        }
        return true
    }


    fun recalculateNextOrDuringLockTimeInsideViewModel(context: Context) {

        Timber.d("■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime開始")


        val newNextOrDuringScheduleLockTime = lockTimeDaoForUse.getNextOrDuringScheduleLockTime()
        if(newNextOrDuringScheduleLockTime!=null){
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.startTimeInLong)
            ))
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.endTimeInLong)
            ))
        }
        val nextOrDuringLockTimeList = nextOrDuringLockTimeDaoForUse.getAll()

        if(nextOrDuringLockTimeList.isNotEmpty()){

            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong)
            ))
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)
            ))
        }

//        GlobalScope.launch(Dispatchers.Main) {
        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime1")

        if (newNextOrDuringScheduleLockTime != null) {
            //今最新状況でデータ投入すべきはず

            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime2")
            if (nextOrDuringLockTimeList.isEmpty()) {
                //今最新状況でロックの最中かつ、もともとデータなかった
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime3")
                nextOrDuringLockTimeDaoForUse.insert(newNextOrDuringScheduleLockTime)

            } else {
                //今最新状況でロックの最中かつ、もともとロック中だった
//
//            Timber.d("★★★★★★★★★★★★★ジャッジ情報★★★★★★★★★★★★★")
//
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)))
//            Timber.d(nextOrDuringLockTimeList[0].endTimeInLong.toString())
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.endTimeInLong)))
//            Timber.d(newNextOrDuringScheduleLockTime.endTimeInLong.toString())
//
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong)))
//            Timber.d(nextOrDuringLockTimeList[0].startTimeInLong.toString())
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.startTimeInLong)))
//            Timber.d(newNextOrDuringScheduleLockTime.startTimeInLong.toString())

                if(nextOrDuringLockTimeList[0].endTimeInLong==newNextOrDuringScheduleLockTime.endTimeInLong
                    &&nextOrDuringLockTimeList[0].startTimeInLong==newNextOrDuringScheduleLockTime.startTimeInLong){
                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime4")
                    //データが変わっていないので処理無し
                }else{

                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime5")
                    nextOrDuringLockTimeDaoForUse.update(newNextOrDuringScheduleLockTime)
                }

            }

        } else {
            //今最新状況でロックの最中ではない
            if (nextOrDuringLockTimeList.isEmpty()) {
                //今最新状況でロックの最中ではない、もともとロック中じゃなかった
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime5")

                //処理無し

            } else {
                //今最新状況でロックの最中ではない、もともとロック中だった
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■nextOrDuringLockTimeDao.deleteAll()実行します")
                nextOrDuringLockTimeDaoForUse.deleteAll()

            }
        }

        Timber.d("■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime終了")
    }
}
