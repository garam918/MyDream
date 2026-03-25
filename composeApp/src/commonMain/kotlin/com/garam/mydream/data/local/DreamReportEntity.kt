package com.garam.mydream.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dream_report_table")
data class DreamReportEntity(

    @PrimaryKey
    val id: String,
    val savedTime : Long,


)
