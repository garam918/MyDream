package com.garam.mydream.core.data.repository

import com.garam.mydream.core.data.model.DreamReport
import com.garam.mydream.core.data.model.ReportFrequency
import com.garam.mydream.core.data.model.ReportScorePoint
import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.database.DreamAnalysisEntity
import kotlinx.datetime.LocalDate

object DreamReportAnalyzer {

    fun analyze(
        type: ReportType,
        periodStart: LocalDate,
        periodEnd: LocalDate,
        dreams: List<DreamAnalysisEntity>
    ): DreamReport {
        val dreamsByDate = dreams.groupBy { it.analysisDate }
        val scorePoints = generateDateRange(periodStart, periodEnd).map { date ->
            ReportScorePoint(
                date = date.toString(),
                score = dreamsByDate[date.toString()]
                    ?.map { it.score }
                    ?.takeIf { it.isNotEmpty() }
                    ?.average()
                    ?.toInt()
            )
        }

        return DreamReport(
            type = type.name,
            periodStart = periodStart.toString(),
            periodEnd = periodEnd.toString(),
            dreamCount = dreams.size,
            scorePoints = scorePoints,
            goodDreamCount = dreams.count { it.score >= GOOD_DREAM_SCORE },
            badDreamCount = dreams.count { it.score < BAD_DREAM_SCORE },
            luckyColors = dreams.toFrequency { it.lucky_color },
            luckyItems = dreams.toFrequency { it.lucky_item }
        )
    }

    fun sourceFingerprint(dreams: List<DreamAnalysisEntity>): String {
        return dreams.map { it.id }.sorted().joinToString(separator = "|")
    }

    private fun List<DreamAnalysisEntity>.toFrequency(
        selector: (DreamAnalysisEntity) -> String
    ): List<ReportFrequency> = map(selector)
        .map(String::trim)
        .filter(String::isNotBlank)
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .take(MAX_FREQUENCY_ROWS)
        .map { ReportFrequency(name = it.key, count = it.value) }

    private fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
        return (start.toEpochDays()..end.toEpochDays()).map(LocalDate::fromEpochDays)
    }

    private const val GOOD_DREAM_SCORE = 80
    private const val BAD_DREAM_SCORE = 60
    private const val MAX_FREQUENCY_ROWS = 5
}
