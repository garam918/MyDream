package com.garam.mydream.core.data.firebase

actual class FirebaseDataSourceProvider {
    actual fun get() : FirebaseDataSource = FirebaseDataSourceImpl()
}