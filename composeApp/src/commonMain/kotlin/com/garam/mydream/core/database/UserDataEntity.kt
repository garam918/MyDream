package com.garam.mydream.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Entity(
    tableName = "user_data"
)
data class UserDataEntity(

    @PrimaryKey
    val uid : String = "",
    val email: String? = "",
    val loginType: String = "",
    val usageCount: Int = 2,
    @ColumnInfo(name = "rewardedChanceUsed", defaultValue = "false") // Specify the default value here
    val rewardedChanceUsed: Boolean = false,
    val paid: Boolean = false,
    val lastUseDate : String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
)
