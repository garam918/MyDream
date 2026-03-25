package com.garam.mydream.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamAnalysisDao {

    @Insert
    suspend fun saveDreamAnalysis(dreamAnalysisEntity: DreamAnalysisEntity)


    @Query("DELETE FROM dream_analysis_table WHERE id = :id")
    suspend fun deleteDreamAnalysis(id: String)


    @Query("SELECT * FROM dream_analysis_table")
    fun getDreamAnalysis() : Flow<List<DreamAnalysisEntity>>

}