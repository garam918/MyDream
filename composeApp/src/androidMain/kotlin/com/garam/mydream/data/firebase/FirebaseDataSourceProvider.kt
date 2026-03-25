package com.garam.mydream.data.firebase

actual class FirebaseDataSourceProvider actual constructor() {
    actual fun get(): FirebaseDataSource = FirebaseDataSourceImpl()
}