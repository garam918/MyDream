package com.garam.mydream.core.database

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(
    tableName = "today_fortune_table",
    primaryKeys = ["uid", "fortuneDate"]
)
@Serializable
data class TodayFortuneEntity(
    val uid: String,
    val fortuneDate: String,
    val title: String,
    val summary: String,
    val score: Int,
    val luckyItems: List<LuckyItemRecommendation>,
    val generatedAt: String
)

@Serializable
data class LuckyItemRecommendation(
    val type: String = "",
    val name: String,
    val description: String,
    val partnerUrl: String
)
