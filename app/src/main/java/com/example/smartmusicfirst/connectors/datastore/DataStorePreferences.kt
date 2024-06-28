package com.example.smartmusicfirst.connectors.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object DataStorePreferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun saveData(context: Context, key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    suspend fun readData(context: Context, key: Preferences.Key<String>): String {
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: ""
    }
}