package com.garam.mydream.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(
    val content: String
)


@Serializable
data class DreamResponse(
    val title: String,
    val score: Int,
    val analysis: String,
    val energy_label: String,
    val energy_percent: Int,
    val good_points: List<String>,
    val warn_points: List<String>,
    val lucky_item: String,
    val lucky_color: String
)