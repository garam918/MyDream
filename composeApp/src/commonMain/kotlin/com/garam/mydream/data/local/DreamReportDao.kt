package com.garam.mydream.data.local

import androidx.room.Dao


@Dao
interface DreamReportDao {

    suspend fun saveDreamReport(dreamReport: DreamReportEntity)

    suspend fun getDreamReport(): DreamReportEntity

}