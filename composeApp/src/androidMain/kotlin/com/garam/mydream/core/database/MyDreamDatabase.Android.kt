package com.garam.mydream.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<MyDreamDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("mydream.db")
    return Room.databaseBuilder<MyDreamDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
