package com.garam.mydream.core.database

import androidx.room.ConstructedBy
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@Database(
    entities = [
        DreamAnalysisEntity::class,
        UserDataEntity::class,
        TodayFortuneEntity::class,
        DreamReportEntity::class
    ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5)
    ]
)
@ConstructedBy(MyDreamDatabaseConstructor::class)
@TypeConverters(DreamTypeConverter::class)
abstract class MyDreamDatabase : RoomDatabase() {

    abstract fun dreamAnalysisDao(): DreamAnalysisDao

    abstract fun userDataDao(): UserDataDao

    abstract fun todayFortuneDao(): TodayFortuneDao

    abstract fun dreamReportDao(): DreamReportDao

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
