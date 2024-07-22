package com.curverto.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curverto.app.models.ExchangeRates
import com.curverto.app.repository.ExchangeRatesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException

class ExchangeRatesViewModel(private val exchangeRatesRepository: ExchangeRatesRepository): ViewModel() {

  private val _exchangeRatesData = MutableStateFlow<ExchangeRates?>(null)
  private val exchangeRatesData : StateFlow<ExchangeRates?> = _exchangeRatesData.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _conversionRates = MutableStateFlow<MutableMap<String, Double>?>(mutableMapOf())
    val conversionRates: StateFlow<MutableMap<String, Double>?> = _conversionRates.asStateFlow()



    fun fetchExchangeRates(base: String) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
          val response = exchangeRatesRepository.getExchangeRates(base)
        if (response.isSuccessful  && response.body() != null) {
          _exchangeRatesData.value = response.body()!!
          _conversionRates.value = exchangeRatesData.value!!.conversion_rates
        } else if (response.errorBody() != null){
          Log.d("CheckingResponse", "fetchExchangeRates: ${response.code()}")
        }
      } catch (e: HttpException) {
        _isLoading.value = false
        Log.e("Exception", "HttpException: ${e.message}")
      } catch (e: IOException) {
        _isLoading.value = false
        Log.e("Exception", "IOException: ${e.message}")
      }
      _isLoading.value = false
    }
    }

    fun getMainCurrency(): String? {
      var currency : String?
      runBlocking {
        currency = exchangeRatesRepository.getMainCurrency()
      }
      return currency
    }



    }

