package com.garam.mydream.data.repository

import com.garam.mydream.data.firebase.FirebaseDataSource
import com.garam.mydream.data.local.DreamAnalysisDao
import com.garam.mydream.data.local.DreamAnalysisEntity
import com.garam.mydream.data.remote.ApiRequest
import com.garam.mydream.data.remote.ApiService
import com.garam.mydream.data.remote.DreamResponse
import kotlinx.coroutines.flow.Flow

class MainRepositoryImpl(

    private val apiService: ApiService,
    private val dreamAnalysisDao: DreamAnalysisDao,
    private val firebaseDataSource: FirebaseDataSource

) : MainRepository {

    override suspend fun sendDreamContent(dreamContent: String): Result<DreamResponse> = try {
        val response = apiService.sendDreamContent(ApiRequest(dreamContent))

        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDreamAnalysis(): Flow<List<DreamAnalysisEntity>> {
        return dreamAnalysisDao.getDreamAnalysis()
    }

    override suspend fun saveDreamAnalysis(dreamAnalysisData : DreamAnalysisEntity) {

        dreamAnalysisDao.saveDreamAnalysis(dreamAnalysisEntity = dreamAnalysisData)
        firebaseDataSource.saveDreamData(dreamData = dreamAnalysisData)
    }

    override suspend fun deleteDreamAnalysis(id: String) {

        dreamAnalysisDao.deleteDreamAnalysis(id = id)
        firebaseDataSource.deleteDreamData(id = id)
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