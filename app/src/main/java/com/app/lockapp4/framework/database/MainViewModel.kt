package com.app.lockapp4.framework.database

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.lockapp4.cancelDeleteInstantLockSchedule
import com.app.lockapp4.restartForDeleteInstantLockSchedule
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
//    nextOrDuringLockTimeDao: NextOrDuringLockTimeDao,
) : ViewModel() {

    var lockTimes = lockTimeDao.getAllFlow()
    var instantLock = instantLockDao.getAllFlow()
//    var nextOrDuringLockTime = nextOrDuringLockTimeDao.getAllFlow()

    var isEmergencyUnlocked by mutableStateOf(false)
    var emergencyTapNumber by  mutableStateOf(0)
    var isLockedByScheduling by mutableStateOf(false)
    var unLockedTimeBySchedulingInLong by mutableStateOf(0L)
    var instantLockTimeOnScreenMinuteInInt by mutableStateOf(1)

    private var lockTimeDaoForUse = lockTimeDao
    private var instantLockDaoForUse = instantLockDao
//    private var nextOrDuringLockTimeDaoForUse = nextOrDuringLockTimeDao

    fun tapEmergencyButton(context: Context){
        emergencyTapNumber += 1
        if(emergencyTapNumber==constEmergencyTapNumberRequired){
            deleteInstantLock()
            isEmergencyUnlocked=true
            emergencyTapNumber=0
            cancelDeleteInstantLockSchedule(context)
        }
    }


    fun updateEnable(lockTime: LockTime): Boolean {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateEnable(lockTime)
            getNextToTimeIfScheduledLocking()
        }
        return true
    }

    fun insertInstantLock(context: Context) {
        val now = Calendar.getInstance()

        Timber.d("■■■■■■■■■■■insertInstantLock開始")
        Timber.d("■■■■■■■■■■■now:"+commonTranslateCalendarToStringYYYYMMDDHHMM(now))


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

        Timber.d("■■■■■■■■■■■"+commonTranslateCalendarToStringYYYYMMDDHHMM(endTime))
        restartForDeleteInstantLockSchedule(context,endTime)

    }

    fun deleteInstantLock() {
        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.deleteAll()
        }
    }

    fun updateToTime(dayId: Int, mHour: Int, mMinute: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateToTime(dayId, mHour, mMinute)
            getNextToTimeIfScheduledLocking()
        }
    }

    fun updateFromTime(dayId: Int, mHour: Int, mMinute: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateFromTime(dayId, mHour, mMinute)
            getNextToTimeIfScheduledLocking()
        }
    }

    private fun getNextToTimeIfScheduledLocking() {

        val unlockedTime = lockTimeDaoForUse.getNextToTimeIfScheduledLocking()

        GlobalScope.launch(Dispatchers.Main) {
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■getNextToTimeIfScheduledLocking はじめ")

            if (unlockedTime != null) {
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■NextTime:"+commonTranslateCalendarToStringYYYYMMDDHHMM(commonTranslateLongToCalendar(unlockedTime!!.timeInMillis)))
                val fromDBTimeInLong=unlockedTime!!.timeInMillis
                if (fromDBTimeInLong != unLockedTimeBySchedulingInLong) {
                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isScheduledLockedCompanionをTrueにする")
                    unLockedTimeBySchedulingInLong = fromDBTimeInLong
                    isLockedByScheduling = true
                }

                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isLockedByScheduling:"+isLockedByScheduling.toString())
            } else {
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■NextTime:NULL")
                if (unLockedTimeBySchedulingInLong != 0L) {
                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isScheduledLockedCompanionをfalseにする")
                    unLockedTimeBySchedulingInLong = 0L
                    isLockedByScheduling = false
                }

                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isLockedByScheduling:"+isLockedByScheduling.toString())
            }
        }
    }
}