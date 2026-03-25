package com.garam.mydream.data.firebase

expect class FirebaseDataSourceProvider() {
    fun get() : FirebaseDataSource
}