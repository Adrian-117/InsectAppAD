package com.example.randominsect.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class SettingsRepository(
    private val context: Context,
) {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }

    // Observe theme mode reactively anywhere in Compose or ViewModels
    val isDarkMode: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[DARK_MODE_KEY] ?: false // default to false/light mode
        }

    // Toggle dark mode
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
