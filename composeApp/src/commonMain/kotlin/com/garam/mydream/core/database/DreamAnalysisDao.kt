package com.garam.mydream.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamAnalysisDao {

    @Insert
    suspend fun saveDreamAnalysis(dreamAnalysisEntity: DreamAnalysisEntity)


    @Query("DELETE FROM dream_analysis_table WHERE id = :id AND uid = :uid")
    suspend fun deleteDreamAnalysis(id: String, uid: String)


    @Query("SELECT * FROM dream_analysis_table WHERE uid = :uid")
    fun getDreamAnalysis(uid: String) : Flow<List<DreamAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDreamAnalysisList(dreamAnalysis: List<DreamAnalysisEntity>)

}
