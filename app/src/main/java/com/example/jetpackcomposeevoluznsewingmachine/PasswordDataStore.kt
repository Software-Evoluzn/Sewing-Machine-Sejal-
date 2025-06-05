package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PasswordDataStore {
    private val Context.dataStore by preferencesDataStore("app_preferences")
    private val PASSWORD_KEY = stringPreferencesKey("user-password")

    suspend fun savePassword(context: Context, password: String) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = password
        }
    }

    fun getPasswordFlow(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PASSWORD_KEY]
        }
    }
}