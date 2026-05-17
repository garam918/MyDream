package com.garam.mydream.core.data.repository

import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.database.DreamAnalysisDao
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.TodayFortuneDao
import com.garam.mydream.core.database.TodayFortuneEntity
import com.garam.mydream.core.localization.AppLanguage
import com.garam.mydream.core.network.ApiRequest
import com.garam.mydream.core.network.ApiService
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.network.TodayFortuneRequest
import com.garam.mydream.core.network.TodayFortuneResponse
import com.garam.mydream.core.settings.AppSettingsStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainRepositoryImpl(

    private val apiService: ApiService,
    private val dreamAnalysisDao: DreamAnalysisDao,
    private val todayFortuneDao: TodayFortuneDao,
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

    override suspend fun getWeeklyDreamAnalysis(): List<DreamResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveWeeklyDreamReport() {
        TODO("Not yet implemented")
    }

    override suspend fun getMonthlyDreamAnalysis(): List<DreamResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveMonthlyDreamReport() {
        TODO("Not yet implemented")
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
}
