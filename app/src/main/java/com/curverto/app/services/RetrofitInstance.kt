package com.curverto.app.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ConversionRatesAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConversionRatesAPI::class.java)
    }
}