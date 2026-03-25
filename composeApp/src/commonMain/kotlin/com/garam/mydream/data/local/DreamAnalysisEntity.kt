package com.garam.mydream.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "dream_analysis_table")
@Serializable
data class DreamAnalysisEntity(

    @PrimaryKey
    val id : String,
    val title: String,
    val score: Int,
    val analysis: String,
    val energy_label: String,
    val energy_percent: Int,
    val good_points: List<String>,
    val warn_points: List<String>,
    val lucky_item: String,
    val lucky_color: String,
    val analysisDate : String
)
