package com.example.time4medapp.ai

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {

    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}