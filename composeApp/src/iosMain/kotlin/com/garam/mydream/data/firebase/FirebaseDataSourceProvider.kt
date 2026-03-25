package com.garam.mydream.data.firebase

actual class FirebaseDataSourceProvider {
    actual fun get() : FirebaseDataSource = FirebaseDataSourceImpl()
}