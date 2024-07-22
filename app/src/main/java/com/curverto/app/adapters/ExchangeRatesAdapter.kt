package com.curverto.app.adapters

import android.content.Context
import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.curverto.app.R
import com.curverto.app.databinding.ExchangeRateLayoutBinding
import com.curverto.app.models.ConversionRate

class ExchangeRatesAdapter(private val context: Context) : RecyclerView.Adapter<ExchangeRatesAdapter.ExchangeRatesViewHolder>() {

    private var defaultUserInput: Double = 1.0
    inner class ExchangeRatesViewHolder(val binding: ExchangeRateLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<ConversionRate>() {
        override fun areItemsTheSame(oldItem: ConversionRate, newItem: ConversionRate): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ConversionRate, newItem: ConversionRate): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var conversionRates: List<ConversionRate>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    fun setUserInput(newUserInput: Double) {
        defaultUserInput = newUserInput
        notifyDataSetChanged()
    }

    fun setFilteredList(filteredList: List<ConversionRate>) {
      this.conversionRates = filteredList
      notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRatesViewHolder {
        return ExchangeRatesViewHolder(
            ExchangeRateLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = conversionRates.size


    override fun onBindViewHolder(holder: ExchangeRatesViewHolder, position: Int) {
        holder.binding.apply {
            val conversionRate = conversionRates[position]
            tvCurrency.text = conversionRate.currency
            tvConversionRate.text = DecimalFormat.getInstance().format(conversionRate.rate * defaultUserInput).toString()
            val resourceId = holder.itemView.context.resources.getIdentifier(conversionRate.currency.lowercase(), "drawable", context.packageName)
            if (resourceId != 0) {
                ivFlag.setImageResource(resourceId)
            } else {
                ivFlag.setImageResource(R.drawable.default_currency)
            }
        }
    }


}