package com.curverto.app.repository

import com.curverto.app.Constants.CURRENCY_KEY
import com.curverto.app.models.ExchangeRates
import com.curverto.app.services.DataStoreImpl
import com.curverto.app.services.RetrofitInstance
import retrofit2.Response

class ExchangeRatesRepository(private val dataStore: DataStoreImpl) {
    suspend fun getExchangeRates(base: String): Response<ExchangeRates> = RetrofitInstance.api.getExchangeRates(base)
    suspend fun getMainCurrency(): String? = dataStore.getString(CURRENCY_KEY)
}