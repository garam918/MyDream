package com.garam.mydream.core.data.repository

import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.TodayFortuneEntity
import com.garam.mydream.core.data.model.DreamReport
import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.network.TodayFortuneResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {


    suspend fun sendDreamContent(dreamContent: String) : Result<DreamResponse>

    suspend fun getDreamAnalysis() : Flow<List<DreamAnalysisEntity>>

    suspend fun saveDreamAnalysis(dreamAnalysisData : DreamAnalysisEntity)

    suspend fun deleteDreamAnalysis(id: String)

    suspend fun getDreamReport(reportType: ReportType): DreamReport?

    suspend fun getTodayFortune(fortuneDate: String): TodayFortuneEntity?

    suspend fun fetchTodayFortune(fortuneDate: String): Result<TodayFortuneResponse>

    suspend fun saveTodayFortune(todayFortuneEntity: TodayFortuneEntity)

}
