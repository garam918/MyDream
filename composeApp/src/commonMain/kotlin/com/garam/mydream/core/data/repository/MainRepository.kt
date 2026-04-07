package com.garam.mydream.core.data.repository

import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.network.DreamResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {


    suspend fun sendDreamContent(dreamContent: String) : Result<DreamResponse>

    suspend fun getDreamAnalysis() : Flow<List<DreamAnalysisEntity>>

    suspend fun saveDreamAnalysis(dreamAnalysisData : DreamAnalysisEntity)

    suspend fun deleteDreamAnalysis(id: String)

    suspend fun getWeeklyDreamAnalysis() : List<DreamResponse>

    suspend fun saveWeeklyDreamReport()

    suspend fun getMonthlyDreamAnalysis() : List<DreamResponse>

    suspend fun saveMonthlyDreamReport()

}