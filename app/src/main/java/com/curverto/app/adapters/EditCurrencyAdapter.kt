package com.curverto.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.curverto.app.Constants.CURRENCY_KEY
import com.curverto.app.databinding.CurrencyLayoutBinding
import com.curverto.app.models.Currency
import com.curverto.app.services.DataStoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EditCurrencyAdapter(context: Context): RecyclerView.Adapter<EditCurrencyAdapter.EditCurrencyViewHolder>() {

    inner class EditCurrencyViewHolder(val binding: CurrencyLayoutBinding): RecyclerView.ViewHolder(binding.root)

    private val datastore = DataStoreImpl(context)

    private val diffCallback = object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var currencies: List<Currency>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    fun setFilteredList(filteredList: List<Currency>) {
        this.currencies = filteredList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditCurrencyViewHolder {
       return EditCurrencyViewHolder(CurrencyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = currencies.size

    override fun onBindViewHolder(holder: EditCurrencyViewHolder, position: Int) {
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
            it.findNavController().previousBackStackEntry?.savedStateHandle?.set("editedCurrencyKey", currency.name)
            it.findNavController().navigateUp()
        }
    }
}