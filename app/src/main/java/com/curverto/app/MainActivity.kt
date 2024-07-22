package com.curverto.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.curverto.app.Constants.CURRENCY_KEY
import com.curverto.app.repository.ExchangeRatesRepository
import com.curverto.app.repository.SettingsRepository
import com.curverto.app.services.DataStoreImpl
import com.curverto.app.viewmodels.ExchangeRatesViewModel
import com.curverto.app.viewmodels.ExchangeRatesViewModelFactory
import com.curverto.app.viewmodels.SettingsViewModel
import com.curverto.app.viewmodels.SettingsViewModelFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    lateinit var exchangeRatesViewModel: ExchangeRatesViewModel
    lateinit var settingsViewModel: SettingsViewModel
     private var mainCurrency: String? = null
    private val dataStore = DataStoreImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { exchangeRatesViewModel.isLoading.value
            }

        }
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        runBlocking {
            mainCurrency = dataStore.getString(CURRENCY_KEY)
        }
        if (mainCurrency != null) {
            graph.setStartDestination(R.id.exchangeRatesFragment)
        } else {
            graph.setStartDestination(R.id.chooseCurrencyFragment)
        }
        navHostFragment.navController.setGraph(graph, intent.extras)
        setupExchangeRatesViewModel()
        setupSettingsViewModel()
    }

  private fun setupExchangeRatesViewModel(){
        val exchangeRatesRepository = ExchangeRatesRepository(dataStore)
        val exchangeRatesFactory = ExchangeRatesViewModelFactory(exchangeRatesRepository)
        exchangeRatesViewModel = ViewModelProvider(this, exchangeRatesFactory)[ExchangeRatesViewModel::class.java]
    }

    private fun setupSettingsViewModel(){
        val settingsRepository = SettingsRepository(dataStore)
        val settingsViewModelFactory = SettingsViewModelFactory(this, settingsRepository)
        settingsViewModel = ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]
    }
}