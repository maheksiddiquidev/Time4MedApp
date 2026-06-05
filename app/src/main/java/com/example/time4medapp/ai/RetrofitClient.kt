package com.example.time4medapp.ai

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    // Read API key from local.properties (runtime)
    private fun getApiKey(): String {
        return try {
            val properties = Properties()
            val localPropertiesFile =
                java.io.File("local.properties")

            if (localPropertiesFile.exists()) {
                properties.load(FileInputStream(localPropertiesFile))
                properties.getProperty("GEMINI_API_KEY") ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    // Logging Interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // API Key Interceptor
    private val apiKeyInterceptor = { chain: okhttp3.Interceptor.Chain ->
        val originalRequest: Request = chain.request()

        val originalUrl: HttpUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("key", getApiKey())
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    // OkHttp Client
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(apiKeyInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Retrofit Instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Gemini API Service
    val geminiApiService: GeminiApiService =
        retrofit.create(GeminiApiService::class.java)
}