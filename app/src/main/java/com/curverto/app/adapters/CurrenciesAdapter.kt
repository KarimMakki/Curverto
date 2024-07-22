package com.curverto.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.curverto.app.Constants.CURRENCY_KEY
import com.curverto.app.SupportedCurrencies
import com.curverto.app.databinding.CurrencyLayoutBinding
import com.curverto.app.fragments.ChooseCurrencyFragmentDirections
import com.curverto.app.models.Currency
import com.curverto.app.services.DataStoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CurrenciesAdapter(context: Context): RecyclerView.Adapter<CurrenciesAdapter.CurrenciesViewHolder>(){

    inner class CurrenciesViewHolder(val binding: CurrencyLayoutBinding): RecyclerView.ViewHolder(binding.root)

   private val currencies =  SupportedCurrencies.currencies.map {
       Currency(it.key, it.value, context.resources!!.getIdentifier(it.key.lowercase(), "drawable", context.packageName))
   }
    private val datastore = DataStoreImpl(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrenciesViewHolder {
       return CurrenciesViewHolder(CurrencyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = currencies.size


    override fun onBindViewHolder(holder: CurrenciesViewHolder, position: Int) {
        val currency = currencies[position]
        holder.binding.apply {
            tvCurrency.text = currency.name
            ivFlag.setImageResource(currency.currencyImage)
        }

        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
             runBlocking {
                 datastore.putString(CURRENCY_KEY, currency.name)
             }
            }
        val direction = ChooseCurrencyFragmentDirections.actionChooseCurrencyFragmentToHomeFragment(currency.name)
            it.findNavController().navigate(direction)
        }
    }


}