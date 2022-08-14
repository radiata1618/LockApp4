package com.app.lockapp4.framework.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "instantLock")
data class InstantLock (
    @PrimaryKey val id: Int,
    val startTimeInLong: Long,
    val durationTimeInLong: Long,
    val endTimeInLong: Long,
): Parcelable


