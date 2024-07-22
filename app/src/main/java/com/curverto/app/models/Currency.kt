package com.curverto.app.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val name: String,
    val country: String,
    @DrawableRes val currencyImage: Int
): Parcelable
