package com.app.lockapp4.framework.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "statusOnScreen")
data class StatusOnScreen (
    @PrimaryKey val id: Int,
    val isLocked: Boolean,
): Parcelable


