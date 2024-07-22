package com.curverto.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curverto.app.repository.SettingsRepository

class SettingsViewModelFactory(private val context: Context, private val settingsRepository: SettingsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(context, settingsRepository) as T
    }
}