package com.curverto.app.models


data class ConversionRate(
    val country: String,
    val currency: String,
    val rate: Double,
)
