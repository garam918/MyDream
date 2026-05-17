package com.garam.mydream.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiService(private val httpClient: HttpClient) {

    companion object {
        private const val DREAM_ANALYSIS_URL = "https://analyzedream-vm676lyjtq-uc.a.run.app"
        private const val TODAY_FORTUNE_FUNCTION_URL = "https://gettodayfortune-vm676lyjtq-uc.a.run.app"
    }

    suspend fun sendDreamContent(dreamContent: ApiRequest) : DreamResponse {
        return httpClient.post(DREAM_ANALYSIS_URL) {
            contentType(ContentType.Application.Json)
            header("Accept-Language", dreamContent.language)
            header("X-App-Language", dreamContent.language)
            setBody(dreamContent)
        }.body()
    }

    suspend fun fetchTodayFortune(request: TodayFortuneRequest): TodayFortuneResponse {
        return httpClient.get(TODAY_FORTUNE_FUNCTION_URL) {
            parameter("date", request.date)
            parameter("language", request.language)
            header("Accept-Language", request.language)
            header("X-App-Language", request.language)
        }.body()
    }
}
