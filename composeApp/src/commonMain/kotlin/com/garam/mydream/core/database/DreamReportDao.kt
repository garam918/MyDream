package com.garam.mydream.core.database

import androidx.room.Dao


@Dao
interface DreamReportDao {

    suspend fun saveDreamReport(dreamReport: DreamReportEntity)

    suspend fun getDreamReport(): DreamReportEntity

}