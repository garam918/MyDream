package com.garam.mydream.core.auth

actual class AuthRepositoryProvider {
    actual fun get(): AuthRepository = AuthRepositoryImpl()
}