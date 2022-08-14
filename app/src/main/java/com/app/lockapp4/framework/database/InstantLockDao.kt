package com.app.lockapp4.framework.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InstantLockDao {
    @Insert
    fun insert(instantLock: InstantLock): Long

    @Update
    fun update(instantLock: InstantLock)

    @Query("delete from instantLock")
    fun deleteAll()

    @Query("select * from instantLock")
    fun getAll(): List<InstantLock>

    @Query("SELECT * FROM instantLock")
    fun getAllFlow(): Flow<List<InstantLock>>

}