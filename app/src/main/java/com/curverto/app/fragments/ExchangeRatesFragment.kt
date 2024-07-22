package com.curverto.app.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.curverto.app.MainActivity
import com.curverto.app.R
import com.curverto.app.SupportedCurrencies
import com.curverto.app.adapters.ExchangeRatesAdapter
import com.curverto.app.databinding.FragmentExchangeratesBinding
import com.curverto.app.models.ConversionRate
import com.curverto.app.viewmodels.ExchangeRatesViewModel
import com.curverto.app.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale

class ExchangeRatesFragment : Fragment() {
    private var exchangeRatesBinding: FragmentExchangeratesBinding? = null
    private val binding get() = exchangeRatesBinding!!
    private lateinit var exchangeRateViewModel: ExchangeRatesViewModel
    private val args by navArgs<ExchangeRatesFragmentArgs>()
    private lateinit var exchangeRatesAdapter: ExchangeRatesAdapter
    private lateinit var settingsViewModel: SettingsViewModel
    private var countries = SupportedCurrencies.currencies
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        exchangeRatesBinding = FragmentExchangeratesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exchangeRateViewModel = (activity as MainActivity).exchangeRatesViewModel
        settingsViewModel = (activity as MainActivity).settingsViewModel
        setupRecyclerView(requireContext())
        var mainCurrency: String = exchangeRateViewModel.getMainCurrency() ?: args.MainCurrencyName

        // get the loading state from viewmodel and update the ui
        collectLatestLifecycleFlow(exchangeRateViewModel.isLoading) {
            binding.pbLoadingRates.visibility = if (it) View.VISIBLE else View.GONE
        }
        // navigate to EditCurrencyFragment
        binding.ivChooseCurrency.setOnClickListener {
            findNavController().navigate(ExchangeRatesFragmentDirections.actionExchangeRatesFragmentToEditCurrencyFragment())
        }

        // get the new currency after choose it in EditCurrencyFragment and navigating back
        lifecycleScope.launch {
            findNavController().currentBackStackEntry?.savedStateHandle?.getStateFlow<String>(
                "editedCurrencyKey",
                mainCurrency
            )?.collect {
                mainCurrency = it
            }
        }

        collectLatestLifecycleFlow(settingsViewModel.isConnected){
           isConnected -> if(isConnected){
            // fetch the exchange rates
            exchangeRateViewModel.fetchExchangeRates(mainCurrency)
            binding.ivNoInternet.visibility = View.GONE
            binding.tvNoInternet.visibility = View.GONE
            binding.rvExchangeRates.visibility = View.VISIBLE
            binding.cvMainCurrency.visibility = View.VISIBLE
           } else {
            binding.ivNoInternet.visibility = View.VISIBLE
            binding.tvNoInternet.visibility = View.VISIBLE
            binding.rvExchangeRates.visibility = View.GONE
            binding.cvMainCurrency.visibility = View.GONE

           }
        }

        collectLatestLifecycleFlow(settingsViewModel.isNightMode){isNightMode ->
            when(isNightMode){
                true -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    setNightTheme()
                }
                false -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        collectLatestLifecycleFlow(exchangeRateViewModel.conversionRates){conversionRates ->
            if(conversionRates != null){
                conversionRates.remove(mainCurrency)
                val conversionRatesList: MutableList<ConversionRate> = mutableListOf()
                conversionRates.onEach {
                   val resourceId =  (activity as MainActivity).resources.getIdentifier(it.key.lowercase(),"drawable", context?.packageName )

                    if (resourceId != 0){
                        conversionRatesList.add(ConversionRate(countries[it.key]!!, it.key, it.value))
                    }

                }
                exchangeRatesAdapter.conversionRates = conversionRatesList
                binding.svSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterList(newText, conversionRatesList)
                        return true
                    }

                })
            }

        }



        binding.ivNightMode.setOnClickListener {
            val currentNightMode = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                settingsViewModel.setIsNightMode(true)
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                settingsViewModel.setIsNightMode(false)

            }
        }

        binding.tvCurrency.text = mainCurrency
        val resourceId = this.resources.getIdentifier(mainCurrency.lowercase(), "drawable", context?.packageName)
        binding.ivFlag.setImageResource(resourceId)

        binding.tvAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString().toDoubleOrNull() ?: 1.0
                exchangeRatesAdapter.setUserInput(userInput)

            }

        })

    }

    private fun setupRecyclerView(context: Context) = binding.rvExchangeRates.apply {
        exchangeRatesAdapter = ExchangeRatesAdapter(context)
        adapter = exchangeRatesAdapter
        layoutManager = LinearLayoutManager(activity)


    }

    private fun filterList(query: String?, mainList: List<ConversionRate>) {
        if (query != null){
            val filteredList = ArrayList<ConversionRate>()
            for (i in mainList){
                // if this condition is satisfied then means that we get what we are searching for
                if (i.currency.lowercase(Locale.ROOT).contains(query) ||i.country.lowercase(Locale.ROOT).contains(query)){
                    // so we will add the item to the newly created list
                    filteredList.add(i)
                }
            }
            if (filteredList.isNotEmpty()){
                exchangeRatesAdapter.setFilteredList(filteredList)
            }
        }
    }


    private fun setNightTheme(){
        val whiteColor = (activity as MainActivity).getColor(R.color.white)
        val blackColor = (activity as MainActivity).getColor(R.color.black)
        binding.ivNightMode.setColorFilter(whiteColor)
        binding.toolbar.setBackgroundColor(blackColor)
        binding.tvTitle.setTextColor(whiteColor)
        binding.ivChooseCurrency.setColorFilter(whiteColor)
        binding.ivNoInternet.setColorFilter(whiteColor)
    }


}

// CREATING A FUNCTION SO WE DON'T HAVE TO REPEAT THE CODE EVERY TIME WE WANT TO COLLECT A FLOW
private fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }


}