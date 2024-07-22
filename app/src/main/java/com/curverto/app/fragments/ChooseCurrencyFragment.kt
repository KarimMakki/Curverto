package com.curverto.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.curverto.app.MainActivity
import com.curverto.app.adapters.CurrenciesAdapter
import com.curverto.app.databinding.FragmentChooseCurrencyBinding


class ChooseCurrencyFragment : Fragment() {
    private var chooseCurrencyBinding: FragmentChooseCurrencyBinding ?= null
    private val binding get() = chooseCurrencyBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       chooseCurrencyBinding = FragmentChooseCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val currenciesAdapter = CurrenciesAdapter((activity as MainActivity))
        binding.rvCurrencies.apply {
            adapter = currenciesAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }


}