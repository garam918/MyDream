package com.garam.mydream.auth

actual class AuthRepositoryProvider {
    actual fun get(): AuthRepository = AuthRepositoryImpl()
}