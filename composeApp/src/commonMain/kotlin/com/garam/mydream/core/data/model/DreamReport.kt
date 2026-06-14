package com.garam.mydream.core.data.model

import kotlinx.serialization.Serializable

enum class ReportType {
    WEEKLY,
    MONTHLY
}

@Serializable
data class DreamReport(
    val type: String,
    val periodStart: String,
    val periodEnd: String,
    val dreamCount: Int,
    val scorePoints: List<ReportScorePoint>,
    val goodDreamCount: Int,
    val badDreamCount: Int,
    val luckyColors: List<ReportFrequency>,
    val luckyItems: List<ReportFrequency>
)

@Serializable
data class ReportScorePoint(
    val date: String,
    val score: Int? = null
)

@Serializable
data class ReportFrequency(
    val name: String,
    val count: Int
)
