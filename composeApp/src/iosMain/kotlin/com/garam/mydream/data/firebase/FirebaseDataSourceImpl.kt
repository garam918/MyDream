package com.garam.mydream.data.firebase

import com.garam.mydream.data.local.DreamAnalysisEntity
import com.garam.mydream.data.local.DreamReportEntity
import com.garam.mydream.data.local.UserDataEntity

class FirebaseDataSourceImpl : FirebaseDataSource {

    override suspend fun setUserData(userDataEntity: UserDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun saveDreamData(dreamData: DreamAnalysisEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun saveDreamReportData(dreamReportEntity: DreamReportEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getDreamData(): List<DreamAnalysisEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getDreamReportData(): List<DreamReportEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDreamData(id: String) {
        TODO("Not yet implemented")
    }
}