package com.garam.mydream.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.data.firebase.FirebaseDataSource
import com.garam.mydream.data.local.UserDataDao
import com.garam.mydream.data.local.UserDataEntity
import kotlinx.coroutines.launch

class LoginViewModel(

    private val userDao : UserDataDao,
    private val firebaseRepo: FirebaseDataSource

) : ViewModel() {


    fun saveUserData(userData: UserDataEntity) = viewModelScope.launch {
        userDao.upsertUserData(userData)
        firebaseRepo.setUserData(userData)
    }

}