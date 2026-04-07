package com.garam.mydream.core.auth

expect class AuthRepositoryProvider() {
    fun get(): AuthRepository
}