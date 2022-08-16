package com.app.lockapp4.framework.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LockTime::class,InstantLock::class
    ,NextOrDuringLockTime::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val lockTimeDatabaseDao: LockTimeDao
    abstract val instantLockDatabaseDao: InstantLockDao
    abstract val nextOrDuringLockTimeDatabaseDao: NextOrDuringLockTimeDao
}