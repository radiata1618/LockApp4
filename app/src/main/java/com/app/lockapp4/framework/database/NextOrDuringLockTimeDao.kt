package com.app.lockapp4.framework.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NextOrDuringLockTimeDao {

    @Insert
    fun insert(nextOrDuringLockTime: NextOrDuringLockTime): Long

    @Update
    fun update(nextOrDuringLockTime: NextOrDuringLockTime)

    @Query("delete from NextOrDuringLockTime")
    fun deleteAll()

    @Query("select * from NextOrDuringLockTime")
    fun getAll(): List<NextOrDuringLockTime>

    @Query("SELECT * FROM NextOrDuringLockTime")
    fun getAllFlow(): Flow<List<NextOrDuringLockTime>>

}