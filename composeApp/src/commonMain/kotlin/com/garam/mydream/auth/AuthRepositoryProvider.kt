package com.garam.mydream.auth

expect class AuthRepositoryProvider() {
    fun get(): AuthRepository
}