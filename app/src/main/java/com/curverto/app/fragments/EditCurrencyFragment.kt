package com.curverto.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.curverto.app.MainActivity
import com.curverto.app.R
import com.curverto.app.SupportedCurrencies
import com.curverto.app.adapters.EditCurrencyAdapter
import com.curverto.app.databinding.FragmentEditCurrencyBinding
import com.curverto.app.models.Currency
import com.curverto.app.viewmodels.SettingsViewModel
import java.util.Locale

class EditCurrencyFragment : Fragment() {
    private var editCurrencyBinding: FragmentEditCurrencyBinding? = null
    private val binding get() = editCurrencyBinding!!
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var editCurrencyAdapter: EditCurrencyAdapter
     private  lateinit var  currenciesList : List<Currency>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editCurrencyBinding = FragmentEditCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = (activity as MainActivity).settingsViewModel
        currenciesList = SupportedCurrencies.currencies.map {
            Currency(it.key, it.value, context?.resources!!.getIdentifier(it.key.lowercase(), "drawable", context?.packageName))
        }
        setupRecyclerView()
        setNightTheme()
        editCurrencyAdapter.currencies = currenciesList
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.svSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
                }
        })
    }

    private fun setupRecyclerView() {
        editCurrencyAdapter = EditCurrencyAdapter((activity as MainActivity))
        binding.rvEditCurrency.apply {
            adapter = editCurrencyAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }

    private fun setNightTheme(){
        if (settingsViewModel.isNightMode.value){
            val whiteColor = (activity as MainActivity).getColor(R.color.white)
            val blackColor = (activity as MainActivity).getColor(R.color.black)
            binding.toolbar.setBackgroundColor(blackColor)
            binding.ivBack.setColorFilter(whiteColor)
            binding.tvEditCurrencyTitle.setTextColor(whiteColor)
        }
    }

    private fun filterList(query: String?) {
        if (query != null){
            val filteredList = ArrayList<Currency>()
            for (i in currenciesList){
                // if this condition is satisfied then means that we get what we are searching for
                if (i.name.lowercase(Locale.ROOT).contains(query) || i.country.lowercase(Locale.ROOT).contains(query)){
                    // so we will add the item to the newly created list
                    filteredList.add(i)
                }
            }
            if (filteredList.isNotEmpty()){
                // if the filtered list is not empty then we have some data that is what
                // the user searched and we will pass the filtered list
                // to the adapter (checksetFilteredList function in adapter class)
                editCurrencyAdapter.setFilteredList(filteredList)
            }
        }
    }

}