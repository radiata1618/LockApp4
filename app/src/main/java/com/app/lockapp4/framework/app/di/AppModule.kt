package com.app.lockapp4.framework.app.di

import LockTimeDao
import LockTimeDatabase
import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@InternalCoroutinesApi
@ExperimentalAnimationApi
//@ExperimentalMaterialNavigationApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideLockTimeDatabase(
//        app: Application
//    ): LockTimeDatabase {
//        return Room.databaseBuilder(
//            app,
//            LockTimeDatabase::class.java,
//            "mainDatabase"
//        ).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideLockTimeDao(database: LockTimeDatabase): LockTimeDao {
//        return database.lockTimeDatabaseDao
//    }

}