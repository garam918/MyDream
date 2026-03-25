package com.garam.mydream.data.firebase

import com.garam.mydream.data.local.UserDataEntity
import com.garam.mydream.data.local.DreamAnalysisEntity
import com.garam.mydream.data.local.DreamReportEntity

interface FirebaseDataSource {


    suspend fun setUserData(userDataEntity: UserDataEntity)

    suspend fun saveDreamData(dreamData: DreamAnalysisEntity)

    suspend fun saveDreamReportData(dreamReportEntity: DreamReportEntity)

    suspend fun getDreamData() : List<DreamAnalysisEntity>

    suspend fun getDreamReportData() : List<DreamReportEntity>

    suspend fun deleteDreamData(id: String)

}