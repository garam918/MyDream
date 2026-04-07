package com.garam.mydream.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.database.DreamAnalysisDao
import com.garam.mydream.core.database.UserDataDao
import com.garam.mydream.core.database.UserDataEntity
import kotlinx.coroutines.launch

class LoginViewModel(

    private val userDao : UserDataDao,
    private val dreamAnalysisDao: DreamAnalysisDao,
    private val firebaseRepo: FirebaseDataSource,
    private val authRepository: AuthRepository

) : ViewModel() {


    fun saveUserData(userData: UserDataEntity) = viewModelScope.launch {
        userDao.upsertUserData(userData)
        firebaseRepo.setUserData(userData)
    }

    suspend fun completeSocialLogin(userData: UserDataEntity): Boolean {
        val isExistingAccount = authRepository.isExistAccount(userData.uid)

        return if (isExistingAccount) {
            syncExistingAccount(userData)
        } else {
            createNewAccount(userData)
        }
    }

    private suspend fun createNewAccount(userData: UserDataEntity): Boolean {
        userDao.upsertUserData(userData)
        firebaseRepo.setUserData(userData)
        return true
    }

    private suspend fun syncExistingAccount(userData: UserDataEntity): Boolean {
        val currentUser = runCatching { authRepository.currentUser() }.getOrNull()
        val syncedUser = currentUser ?: userData
        val dreamAnalysisList = runCatching { firebaseRepo.getDreamData() }.getOrDefault(emptyList())

        userDao.upsertUserData(syncedUser)

        if (dreamAnalysisList.isNotEmpty()) {
            dreamAnalysisDao.saveDreamAnalysisList(dreamAnalysisList)
        }

        return true
    }

}
