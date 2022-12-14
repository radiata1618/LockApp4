package com.app.lockapp4.framework.app.di

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.room.Room
import com.app.lockapp4.framework.database.*
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

    @Provides
    @Singleton
    fun provideAppDatabase(
        app: Application
    ): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "mainDatabase"
        ).apply {
            allowMainThreadQueries()
            addMigrations(MIGRATION_1_2)
        }.build()
    }

    @Provides
    @Singleton
    fun provideLockTimeDao(database: AppDatabase): LockTimeDao {
        return database.lockTimeDatabaseDao
    }

    @Provides
    @Singleton
    fun provideInstantLockDao(database: AppDatabase): InstantLockDao {
        return database.instantLockDatabaseDao
    }

    @Provides
    @Singleton
    fun provideNextOrDuringLockTimeDao(database: AppDatabase): NextOrDuringLockTimeDao {
        return database.nextOrDuringLockTimeDatabaseDao
    }

    @Provides
    @Singleton
    fun provideStatusOnScreenDao(database: AppDatabase): StatusOnScreenDao {
        return database.statusOnScreenDao
    }
}