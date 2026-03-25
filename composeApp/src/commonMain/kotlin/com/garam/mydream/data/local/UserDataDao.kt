package com.garam.mydream.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert


@Dao
interface UserDataDao {

    @Upsert
    suspend fun upsertUserData(userData: UserDataEntity)


    @Query("SELECT * FROM user_data WHERE uid = :uid")
    suspend fun getUserData(uid: String) : UserDataEntity?


}