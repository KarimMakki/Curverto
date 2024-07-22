package com.curverto.app.repository

import com.curverto.app.Constants.IS_NIGHT_MODE_KEY
import com.curverto.app.services.DataStoreImpl

class SettingsRepository(private val dataStore: DataStoreImpl) {
    suspend fun setIsNightMode(isNightMode: Boolean) =
        dataStore.putBoolean(IS_NIGHT_MODE_KEY, isNightMode)

    suspend fun getNightModeState(): Boolean = dataStore.getBoolean(IS_NIGHT_MODE_KEY)

}