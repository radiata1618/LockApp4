package com.app.lockapp4.framework.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//https://blog.mokelab.com/
@HiltViewModel
class LockTimeViewModel @Inject constructor(
lockTimeDao: LockTimeDao,
) : ViewModel(){

    var lockTimes = lockTimeDao.getAllFlow()

    var isInstantlyLockedCompanion = false
    var isScheduledLockedCompanion = false

    var dao=lockTimeDao


}