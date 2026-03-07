package com.garam.mydream.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.garam.mydream.data.local.MyDreamDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<MyDreamDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("mydream.db")
    return Room.databaseBuilder<MyDreamDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}