package com.garam.mydream.core.data.repository

import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.database.DreamAnalysisDao
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.network.ApiRequest
import com.garam.mydream.core.network.ApiService
import com.garam.mydream.core.network.DreamResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainRepositoryImpl(

    private val apiService: ApiService,
    private val dreamAnalysisDao: DreamAnalysisDao,
    private val firebaseDataSource: FirebaseDataSource,
    private val authRepository: AuthRepository

) : MainRepository {

    override suspend fun sendDreamContent(dreamContent: String): Result<DreamResponse> = try {
        val response = apiService.sendDreamContent(ApiRequest(dreamContent))

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
}
