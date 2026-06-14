package com.garam.mydream.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DreamReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDreamReport(dreamReport: DreamReportEntity)

    @Query(
        """SELECT * FROM dream_report_table
            WHERE uid = :uid AND reportType = :reportType AND periodStart = :periodStart
            LIMIT 1"""
    )
    suspend fun getDreamReport(
        uid: String,
        reportType: String,
        periodStart: String
    ): DreamReportEntity?
}
