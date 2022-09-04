package com.app.lockapp4.framework.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LockTime::class, InstantLock::class, NextOrDuringLockTime::class, StatusOnScreen::class
    ], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val lockTimeDatabaseDao: LockTimeDao
    abstract val instantLockDatabaseDao: InstantLockDao
    abstract val nextOrDuringLockTimeDatabaseDao: NextOrDuringLockTimeDao
    abstract val statusOnScreenDao: StatusOnScreenDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE statusOnScreen (id integer PRIMARY KEY NOT NULL, isLocked INTEGER NOT NULL)")

    }
}