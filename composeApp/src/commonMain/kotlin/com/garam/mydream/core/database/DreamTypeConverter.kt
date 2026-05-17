package com.garam.mydream.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DreamTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?: emptyList()
    }

    @TypeConverter
    fun fromLuckyItemList(value: List<LuckyItemRecommendation>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toLuckyItemList(value: String): List<LuckyItemRecommendation> {
        return value
            .takeIf { it.isNotBlank() }
            ?.let { json.decodeFromString<List<LuckyItemRecommendation>>(it) }
            ?: emptyList()
    }
}
