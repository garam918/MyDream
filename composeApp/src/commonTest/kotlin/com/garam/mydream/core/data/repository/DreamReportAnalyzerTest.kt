package com.garam.mydream.core.data.repository

import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.database.DreamAnalysisEntity
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class DreamReportAnalyzerTest {

    @Test
    fun weeklyReportGroupsDailyScoresAndFrequencies() {
        val dreams = listOf(
            dream("1", "2026-06-08", 80, "Blue", "Key"),
            dream("2", "2026-06-08", 60, "Blue", "Book"),
            dream("3", "2026-06-10", 40, "Gold", "Key")
        )

        val report = DreamReportAnalyzer.analyze(
            type = ReportType.WEEKLY,
            periodStart = LocalDate(2026, 6, 8),
            periodEnd = LocalDate(2026, 6, 14),
            dreams = dreams
        )

        assertEquals(7, report.scorePoints.size)
        assertEquals(70, report.scorePoints.first().score)
        assertNull(report.scorePoints[1].score)
        assertEquals(40, report.scorePoints[2].score)
        assertEquals(1, report.goodDreamCount)
        assertEquals(1, report.badDreamCount)
        assertEquals("Blue", report.luckyColors.first().name)
        assertEquals(2, report.luckyColors.first().count)
        assertEquals("Key", report.luckyItems.first().name)
    }

    @Test
    fun fingerprintChangesWhenDreamIsAddedOrDeleted() {
        val first = dream("1", "2026-06-08", 80, "Blue", "Key")
        val second = dream("2", "2026-06-09", 70, "Gold", "Book")

        val oneDreamFingerprint = DreamReportAnalyzer.sourceFingerprint(listOf(first))
        val twoDreamFingerprint = DreamReportAnalyzer.sourceFingerprint(listOf(first, second))

        assertNotEquals(oneDreamFingerprint, twoDreamFingerprint)
        assertEquals(oneDreamFingerprint, DreamReportAnalyzer.sourceFingerprint(listOf(first)))
    }

    private fun dream(
        id: String,
        date: String,
        score: Int,
        color: String,
        item: String
    ) = DreamAnalysisEntity(
        id = id,
        uid = "user",
        title = "title",
        score = score,
        analysis = "analysis",
        energy_label = "positive",
        energy_percent = score,
        good_points = emptyList(),
        warn_points = emptyList(),
        lucky_item = item,
        lucky_color = color,
        analysisDate = date
    )
}
