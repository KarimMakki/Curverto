package com.curverto.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curverto.app.repository.ExchangeRatesRepository

class ExchangeRatesViewModelFactory(private val exchangeRatesRepository: ExchangeRatesRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExchangeRatesViewModel(exchangeRatesRepository) as T
    }
}