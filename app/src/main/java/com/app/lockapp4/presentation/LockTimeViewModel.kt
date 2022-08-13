package com.app.lockapp4.presentation

import LockTimeDao
import LockTime
import LockTimeDatabase
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.room.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//https://blog.mokelab.com/
@HiltViewModel
class LockTimeViewModel @Inject constructor(
//lockTimeDao: LockTimeDao,
//app: Application
) : ViewModel(){

//    var lockTimes = lockTimeDao.getAllFlow()

    var lockTimes = Room.databaseBuilder(
            Application(),
            LockTimeDatabase::class.java,
            "mainDatabase"
        ).build().lockTimeDatabaseDao.getAllFlow()
}