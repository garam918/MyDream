package com.garam.mydream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.auth.AuthRepository
import com.garam.mydream.data.local.UserDataEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserDataEntity?>(null)
    val currentUser = _currentUser.asStateFlow()


    init {
        refreshCurrentUser()
    }

    fun refreshCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.currentUser()
        }
    }

    suspend fun updateUser(user: UserDataEntity) = authRepository.updateUserInfo(user)



    fun getLoggedIn() = authRepository.isLoggedIn()

}
