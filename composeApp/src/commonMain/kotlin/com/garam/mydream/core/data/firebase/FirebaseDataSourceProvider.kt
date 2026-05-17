package com.garam.mydream.core.data.firebase

expect class FirebaseDataSourceProvider() {
    fun get() : FirebaseDataSource
}