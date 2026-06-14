package com.garam.mydream.core.data.repository

import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.database.DreamAnalysisDao
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.TodayFortuneDao
import com.garam.mydream.core.database.TodayFortuneEntity
import com.garam.mydream.core.database.DreamReportDao
import com.garam.mydream.core.database.DreamReportEntity
import com.garam.mydream.core.data.model.DreamReport
import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.localization.AppLanguage
import com.garam.mydream.core.network.ApiRequest
import com.garam.mydream.core.network.ApiService
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.network.TodayFortuneRequest
import com.garam.mydream.core.network.TodayFortuneResponse
import com.garam.mydream.core.settings.AppSettingsStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MainRepositoryImpl(

    private val apiService: ApiService,
    private val dreamAnalysisDao: DreamAnalysisDao,
    private val todayFortuneDao: TodayFortuneDao,
    private val dreamReportDao: DreamReportDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val authRepository: AuthRepository,
    private val appSettingsStorage: AppSettingsStorage

) : MainRepository {

    override suspend fun sendDreamContent(dreamContent: String): Result<DreamResponse> = try {
        val response = apiService.sendDreamContent(
            ApiRequest(
                content = dreamContent,
                language = currentLanguageTag()
            )
        )

        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDreamAnalysis(): Flow<List<DreamAnalysisEntity>> {
        val uid = authRepository.currentUser()?.uid ?: return flowOf(emptyList())
        return dreamAnalysisDao.getDreamAnalysis(uid = uid)
    }

    override suspend fun saveDreamAnalysis(dreamAnalysisData : DreamAnalysisEntity) {
        val uid = authRepository.currentUser()?.uid ?: return
        val dreamAnalysisEntity = dreamAnalysisData.copy(uid = uid)

        dreamAnalysisDao.saveDreamAnalysis(dreamAnalysisEntity = dreamAnalysisEntity)
        firebaseDataSource.saveDreamData(dreamData = dreamAnalysisEntity)
    }

    override suspend fun deleteDreamAnalysis(id: String) {
        val uid = authRepository.currentUser()?.uid ?: return

        firebaseDataSource.deleteDreamData(id = id)
        dreamAnalysisDao.deleteDreamAnalysis(id = id, uid = uid)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getDreamReport(reportType: ReportType): DreamReport? {
        val uid = authRepository.currentUser()?.uid ?: return null
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val (periodStart, periodEnd) = reportPeriod(reportType, today)
        val dreams = dreamAnalysisDao.getDreamAnalysisBetween(
            uid = uid,
            startDate = periodStart.toString(),
            endDate = periodEnd.toString()
        )
        val fingerprint = DreamReportAnalyzer.sourceFingerprint(dreams)
        val cached = dreamReportDao.getDreamReport(
            uid = uid,
            reportType = reportType.name,
            periodStart = periodStart.toString()
        )

        if (cached?.sourceFingerprint == fingerprint) {
            return runCatching { reportJson.decodeFromString<DreamReport>(cached.payload) }
                .getOrNull()
        }

        val report = DreamReportAnalyzer.analyze(reportType, periodStart, periodEnd, dreams)
        dreamReportDao.saveDreamReport(
            DreamReportEntity(
                uid = uid,
                reportType = reportType.name,
                periodStart = periodStart.toString(),
                periodEnd = periodEnd.toString(),
                sourceFingerprint = fingerprint,
                payload = reportJson.encodeToString(report),
                savedTime = Clock.System.now().toEpochMilliseconds()
            )
        )
        return report
    }

    override suspend fun getTodayFortune(fortuneDate: String): TodayFortuneEntity? {
        val uid = authRepository.currentUser()?.uid ?: return null
        return todayFortuneDao.getTodayFortune(uid = uid, fortuneDate = fortuneDate)
    }

    override suspend fun fetchTodayFortune(fortuneDate: String): Result<TodayFortuneResponse> = try {
        Result.success(
            apiService.fetchTodayFortune(
                TodayFortuneRequest(
                    date = fortuneDate,
                    language = currentLanguageTag()
                )
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveTodayFortune(todayFortuneEntity: TodayFortuneEntity) {
        val uid = authRepository.currentUser()?.uid ?: return
        todayFortuneDao.saveTodayFortune(todayFortuneEntity.copy(uid = uid))
    }

    private fun currentLanguageTag(): String {
        val languageName = appSettingsStorage.getLanguageName()
            ?: appSettingsStorage.getSystemLanguageName()
        val language = AppLanguage.entries.firstOrNull { it.name == languageName }
            ?: AppLanguage.ENGLISH
        return language.languageTag
    }

    private fun reportPeriod(type: ReportType, today: LocalDate): Pair<LocalDate, LocalDate> {
        return when (type) {
            ReportType.WEEKLY -> {
                val start = LocalDate.fromEpochDays(today.toEpochDays() - today.dayOfWeek.ordinal)
                start to LocalDate.fromEpochDays(start.toEpochDays() + 6)
            }
            ReportType.MONTHLY -> {
                val start = LocalDate(today.year, today.month, 1)
                val end = LocalDate(today.year, today.month, daysInMonth(today.year, today.month.ordinal + 1))
                start to end
            }
        }
    }

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        2 -> if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    private companion object {
        val reportJson = Json { ignoreUnknownKeys = true }
    }
}
