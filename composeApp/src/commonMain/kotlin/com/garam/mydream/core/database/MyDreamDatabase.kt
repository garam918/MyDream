package com.garam.mydream.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.AutoMigration
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@Database(
    entities = [DreamAnalysisEntity::class, UserDataEntity::class],
    version = 1,
    exportSchema = true,
//    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)]
)
@ConstructedBy(MyDreamDatabaseConstructor::class)
@TypeConverters(DreamTypeConverter::class)
abstract class MyDreamDatabase : RoomDatabase() {

    abstract fun dreamAnalysisDao(): DreamAnalysisDao

    abstract fun userDataDao(): UserDataDao

}

@Suppress("KotlinNoActualForExpect")
expect object MyDreamDatabaseConstructor : RoomDatabaseConstructor<MyDreamDatabase> {
    override fun initialize(): MyDreamDatabase
}

fun getMyDreamDatabase(
    builder: RoomDatabase.Builder<MyDreamDatabase>
): MyDreamDatabase = builder.setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
