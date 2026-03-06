package com.garam.mydream.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiService(private val httpClient: HttpClient) {

    suspend fun sendDreamContent(dreamContent: ApiRequest) : DreamResponse {
        return httpClient.post("https://analyzedream-vm676lyjtq-uc.a.run.app") {
            contentType(ContentType.Application.Json)
            setBody(dreamContent)
        }.body()
    }
}