package com.app.lockapp4.framework.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusOnScreenDao {
    @Insert
    fun insert(statusOnScreen: StatusOnScreen): Long

    @Update
    fun update(statusOnScreen: StatusOnScreen)

    @Query("delete from statusOnScreen")
    fun deleteAll()

    @Query("select * from statusOnScreen")
    fun getAll(): List<StatusOnScreen>

    @Query("SELECT * FROM statusOnScreen")
    fun getAllFlow(): Flow<List<StatusOnScreen>>

}