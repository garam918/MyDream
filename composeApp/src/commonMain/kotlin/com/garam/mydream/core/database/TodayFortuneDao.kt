package com.garam.mydream.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TodayFortuneDao {

    @Query("SELECT * FROM today_fortune_table WHERE uid = :uid AND fortuneDate = :fortuneDate LIMIT 1")
    suspend fun getTodayFortune(uid: String, fortuneDate: String): TodayFortuneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTodayFortune(todayFortuneEntity: TodayFortuneEntity)
}
