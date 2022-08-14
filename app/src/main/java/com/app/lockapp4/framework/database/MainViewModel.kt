package com.app.lockapp4.framework.database

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
) : ViewModel() {

    var lockTimes = lockTimeDao.getAllFlow()
    var instantLock = instantLockDao.getAllFlow()

    var isScheduledLockedCompanion by mutableStateOf(false)
    var unLockedTimeByScheduling by mutableStateOf("")
    var instantLockTimeOnScreen by mutableStateOf(1)

    private var lockTimeDaoForUse = lockTimeDao
    private var instantLockDaoForUse = instantLockDao

    fun initialProcess() {
        GlobalScope.launch(Dispatchers.IO) {
            val lockData = lockTimeDaoForUse.getAll()
            if (lockData.isEmpty()) {
                lockTimeDaoForUse.insertAllDefaultData()
            }

            var instantLockList =instantLockDaoForUse.getAll()
            if(instantLockList.isNotEmpty()){

                val now = Calendar.getInstance()

                val endTime = Calendar.getInstance()
                val date = Date(instantLockList[0].startTimeInLong)
                endTime.time = date
                endTime.add(Calendar.MINUTE,instantLockList[0].durationTimeInLong.toInt())

                if(now!!.after(endTime!!)){

                    instantLockDaoForUse.deleteAll()

                }
            }
        }
    }

    fun updateEnable(lockTime: LockTime): Boolean {
        GlobalScope.launch(Dispatchers.IO) {
            lockTimeDaoForUse.updateEnable(lockTime)
            getNextToTimeIfScheduledLocking()
        }
        return true
    }

    fun insertInstantLock() {
        val now = Calendar.getInstance()

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.MINUTE, instantLockTimeOnScreen)

        GlobalScope.launch(Dispatchers.IO) {
            instantLockDaoForUse.insert(
                InstantLock(
                    0,
                    now.timeInMillis,
                    instantLockTimeOnScreen.toLong()
                )
            )
        }

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
                val tmpUnLockedTimeByScheduling =
                    SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(unlockedTime.time)
                if (tmpUnLockedTimeByScheduling != unLockedTimeByScheduling) {
                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isScheduledLockedCompanionをTrueにする")
                    unLockedTimeByScheduling = tmpUnLockedTimeByScheduling
                    isScheduledLockedCompanion = true
                }

            } else {

                if (unLockedTimeByScheduling != "") {
                    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■isScheduledLockedCompanionをfalseにする")
                    unLockedTimeByScheduling = ""
                    isScheduledLockedCompanion = false
                }
            }
        }
    }
}