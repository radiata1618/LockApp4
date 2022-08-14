package com.app.lockapp4.framework.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
lockTimeDao: LockTimeDao,
) : ViewModel(){

    var lockTimes = lockTimeDao.getAllFlow()

//    var isInstantlyLockedCompanion = false
//    var isScheduledLockedCompanion = false

    var lockTimeDaoForUse=lockTimeDao

    fun makeInitialAllData(): Boolean {
        val lockData = lockTimeDaoForUse.getAll()
        if(lockData.isEmpty()){
            lockTimeDaoForUse.insertAllDefaultData()
            return true
        }
        return false
    }

}