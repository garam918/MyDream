package com.garam.mydream.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.garam.mydream.data.local.MyDreamDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<MyDreamDatabase> {
    val dbFilePath = documentDirectory() + "/mydream.db"
    return Room.databaseBuilder<MyDreamDatabase>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}