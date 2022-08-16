package com.app.lockapp4.framework.database

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.lockapp4.cancelDeleteInstantLockSchedule
import com.app.lockapp4.restartForDeleteInstantLockSchedule
import com.app.lockapp4.framework.utl.commonTranslateCalendarToStringYYYYMMDDHHMM
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

    fun tapEmergencyButton(context: Context) {
        emergencyTapNumber += 1
        if (emergencyTapNumber == constEmergencyTapNumberRequired) {
            deleteInstantLock()
            isEmergencyUnlocked = true
            emergencyTapNumber = 0
            cancelDeleteInstantLockSchedule(context)
        }
    }


    fun updateEnable(lockTime: LockTime): Boolean {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateEnable(lockTime)
            recalculateNextOrDuringLockTime()
        }
        return true
    }

    fun insertInstantLock(context: Context) {
        val now = Calendar.getInstance()

        Timber.d("■■■■■■■■■■■insertInstantLock開始")
        Timber.d("■■■■■■■■■■■now:" + commonTranslateCalendarToStringYYYYMMDDHHMM(now))


        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.insert(
                InstantLock(
                    0,
                    now.timeInMillis,
                    instantLockTimeOnScreenMinuteInInt.toLong()
                )
            )
        }

        val endTime = Calendar.getInstance()
        endTime.timeInMillis = now.timeInMillis
        endTime.add(Calendar.MINUTE, instantLockTimeOnScreenMinuteInInt)

        Timber.d("■■■■■■■■■■■" + commonTranslateCalendarToStringYYYYMMDDHHMM(endTime))
        restartForDeleteInstantLockSchedule(context, endTime)

    }

    fun deleteInstantLock() {
        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.deleteAll()
        }
    }

    fun updateToTime(dayId: Int, mHour: Int, mMinute: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateToTime(dayId, mHour, mMinute)
            recalculateNextOrDuringLockTime()
        }
    }

    fun updateFromTime(dayId: Int, mHour: Int, mMinute: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateFromTime(dayId, mHour, mMinute)
            recalculateNextOrDuringLockTime()
        }
    }

    private fun recalculateNextOrDuringLockTime() {

        val newNextOrDuringScheduleLockTime = lockTimeDaoForUse.getNextOrDuringScheduleLockTime()
        val nextOrDuringLockTimeList = nextOrDuringLockTimeDaoForUse.getAll()

//        GlobalScope.launch(Dispatchers.Main) {
        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■getNextToTimeIfScheduledLocking はじめ")

        if (newNextOrDuringScheduleLockTime != null) {
            //今最新状況でデータ投入すべきはず

            if (nextOrDuringLockTimeList.isEmpty()) {
                //今最新状況でロックの最中かつ、もともとデータなかった
                nextOrDuringLockTimeDaoForUse.insert(newNextOrDuringScheduleLockTime)

            } else {
                //今最新状況でロックの最中かつ、もともとロック中だった
                if(nextOrDuringLockTimeList[0].endTimeInLong==newNextOrDuringScheduleLockTime.endTimeInLong
                    &&nextOrDuringLockTimeList[0].startTimeInLong==newNextOrDuringScheduleLockTime.startTimeInLong){
                    //データが変わっていないので処理無し
                }else{
                    nextOrDuringLockTimeDaoForUse.update(newNextOrDuringScheduleLockTime)
                }

            }

        } else {
            //今最新状況でロックの最中ではない
            if (nextOrDuringLockTimeList.isEmpty()) {
                //今最新状況でロックの最中ではない、もともとロック中じゃなかった

                //処理無し

            } else {
                //今最新状況でロックの最中ではない、もともとロック中だった
                nextOrDuringLockTimeDaoForUse.deleteAll()

            }
        }

    }

}