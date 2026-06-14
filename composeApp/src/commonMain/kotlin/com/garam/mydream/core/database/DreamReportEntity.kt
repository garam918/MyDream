package com.garam.mydream.core.database

import androidx.room.Entity

@Entity(
    tableName = "dream_report_table",
    primaryKeys = ["uid", "reportType", "periodStart"]
)
data class DreamReportEntity(
    val uid: String,
    val reportType: String,
    val periodStart: String,
    val periodEnd: String,
    val sourceFingerprint: String,
    val payload: String,
    val savedTime: Long
)
