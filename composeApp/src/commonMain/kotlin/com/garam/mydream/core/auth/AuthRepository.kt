package com.garam.mydream.core.auth

import com.garam.mydream.core.database.UserDataEntity

interface AuthRepository {

    fun isLoggedIn() : Boolean

    suspend fun isExistAccount(uid: String) : Boolean

    suspend fun signInAnonymously(): UserDataEntity?
    suspend fun signInWithGoogle(idToken: String, accessToken : String): UserDataEntity?

    suspend fun linkInWithApple() : UserDataEntity?

    suspend fun signInWithApple(): UserDataEntity?
    suspend fun signOut()

    suspend fun reAuthenticate(idToken: String, accessToken : String) : Boolean

    suspend fun deleteAccount()
    suspend fun currentUser(): UserDataEntity?

    suspend fun updateUserInfo(userInfo: UserDataEntity)

}