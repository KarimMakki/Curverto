package com.curverto.app.services

import com.curverto.app.Constants.API_KEY
import com.curverto.app.models.ExchangeRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ConversionRatesAPI {

    @GET("/v6/$API_KEY/latest/{base}")
     suspend fun getExchangeRates(@Path("base") base: String): Response<ExchangeRates>
}