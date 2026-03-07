package com.garam.mydream.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@Database(entities = [DreamAnalysisEntity::class], version = 1, exportSchema = true)
@ConstructedBy(MyDreamDatabaseConstructor::class)
@TypeConverters(DreamTypeConverter::class)
abstract class MyDreamDatabase : RoomDatabase() {

 abstract fun dreamAnalysisDao() : DreamAnalysisDao

}

@Suppress("KotlinNoActualForExpect")
expect object MyDreamDatabaseConstructor : RoomDatabaseConstructor<MyDreamDatabase> {
    override fun initialize(): MyDreamDatabase
}

fun getMyDreamDatabase(
    builder: RoomDatabase.Builder<MyDreamDatabase>
) : MyDreamDatabase = builder.setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()