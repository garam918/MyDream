package com.garam.mydream.core.auth

import com.garam.mydream.core.database.UserDataEntity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    override fun isLoggedIn(): Boolean = Firebase.auth.currentUser != null

    override suspend fun isExistAccount(uid: String): Boolean {
        return Firebase.firestore.collection("Users").document(uid).get().await().exists()
    }

    override suspend fun signInAnonymously(): UserDataEntity? {
        val user = Firebase.auth.signInAnonymously().await().user
        return UserDataEntity(
            uid = user?.uid.toString(),
            email = "Guest",
            loginType = "anonymous",
            usageCount = 2,
            rewardedChanceUsed = false,
            paid = false
        )
    }

    override suspend fun signInWithGoogle(
        idToken: String,
        accessToken: String
    ): UserDataEntity? {
        val googleCredential = GoogleAuthProvider.getCredential(idToken, null)

        if(Firebase.auth.currentUser != null && Firebase.auth.currentUser?.isAnonymous == true) {

            val task = Firebase.auth.currentUser?.linkWithCredential(googleCredential)

            var user : FirebaseUser?

            if(task?.isSuccessful == true) {
                user = Firebase.auth.currentUser?.linkWithCredential(googleCredential)?.await()?.user
            }
            else {
                user = Firebase.auth.signInWithCredential(googleCredential).await().user
            }

            println("google user $user")


            val email = user?.email ?: user?.providerData[1]?.email
            val uid = user?.uid.toString()
            val loginType = "google"

            return UserDataEntity(
                uid = uid,
                email = email,
                loginType = loginType,
                usageCount = 2,
                rewardedChanceUsed = false,
                paid = false
            )
        }
        else {

            val user = Firebase.auth.signInWithCredential(googleCredential).await().user

            val email = user?.email
            val uid = user?.uid.toString()
            val loginType = "google"

            return UserDataEntity(
                uid = uid,
                email = email,
                loginType = loginType,
                usageCount = 2,
                rewardedChanceUsed = false
            )
        }
    }

    override suspend fun linkInWithApple(): UserDataEntity? = null

    override suspend fun signInWithApple(): UserDataEntity? = null

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun reAuthenticate(
        idToken: String,
        accessToken: String
    ): Boolean {
        val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
        return Firebase.auth.signInWithCredential(googleCredential).await().user != null
    }

    override suspend fun deleteAccount() {
        val currentUser = Firebase.auth.currentUser

        Firebase.firestore.collection("Users").document(currentUser?.uid.toString())
            .delete().await()

        currentUser?.delete()?.await()
    }

    override suspend fun currentUser(): UserDataEntity? {
        val user = Firebase.auth.currentUser

        return if(user == null) null
        else {
            if(user?.isAnonymous == true) {
                UserDataEntity(uid = user.uid, email = user.email, loginType = "anonymous", paid = false)
            }
            else {
                Firebase.firestore.collection("Users").document(user.uid)
                    .get().await().toObject(UserDataEntity::class.java)
            }


        }

//        val loginType = if(user?.isAnonymous == true) "anonymous"
//        else when(user?.providerData[1]?.providerId) {
//            "google.com" -> "google"
//            "apple.com" -> "apple"
//            else -> ""
//        }
//
//        return if(user == null) null else UserDataEntity(uid = user.uid, email = user.email, loginType = loginType)
    }

    override suspend fun updateUserInfo(userInfo: UserDataEntity) {
        Firebase.firestore.collection("Users").document(userInfo.uid)
            .update(
                "paid", userInfo.paid,
                "lastUseDate", userInfo.lastUseDate,
                "usageCount", userInfo.usageCount,
                "rewardedChanceUsed", userInfo.rewardedChanceUsed
            )
    }
}
