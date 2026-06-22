package com.garam.mydream.core.data.firebase

import com.garam.mydream.core.database.UserDataEntity
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.DreamReportEntity

interface FirebaseDataSource {


    suspend fun setUserData(userDataEntity: UserDataEntity)

    suspend fun saveDreamData(dreamData: DreamAnalysisEntity)

    suspend fun saveDreamReportData(dreamReportEntity: DreamReportEntity)

    suspend fun getDreamData() : List<DreamAnalysisEntity>

    suspend fun getDreamReportData() : List<DreamReportEntity>

    suspend fun deleteDreamData(id: String)

}
