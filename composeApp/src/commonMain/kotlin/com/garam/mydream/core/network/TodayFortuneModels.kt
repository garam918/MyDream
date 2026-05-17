package com.garam.mydream.core.network

import kotlinx.serialization.Serializable

@Serializable
data class TodayFortuneRequest(
    val date: String,
    val language: String
)

@Serializable
data class TodayFortuneResponse(
    val type: String,
    val label: String,
    val content: String,
    val lucky: TodayFortuneLuckyResponse
)

@Serializable
data class TodayFortuneLuckyResponse(
    val number: TodayFortuneLuckyNumberResponse,
    val item: TodayFortuneLuckyNamedResponse,
    val color: TodayFortuneLuckyNamedResponse
)

@Serializable
data class TodayFortuneLuckyNumberResponse(
    val value: String,
    val reason: String
)

@Serializable
data class TodayFortuneLuckyNamedResponse(
    val name: String,
    val reason: String
)
