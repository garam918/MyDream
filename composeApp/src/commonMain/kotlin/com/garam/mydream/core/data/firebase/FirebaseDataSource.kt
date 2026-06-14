package com.garam.mydream.core.data.firebase

import com.garam.mydream.core.database.UserDataEntity
import com.garam.mydream.core.database.DreamAnalysisEntity

interface FirebaseDataSource {


    suspend fun setUserData(userDataEntity: UserDataEntity)

    suspend fun saveDreamData(dreamData: DreamAnalysisEntity)

    suspend fun getDreamData() : List<DreamAnalysisEntity>

    suspend fun deleteDreamData(id: String)

}
