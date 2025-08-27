package com.thmanyah.task.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thmanyah.task.domain.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguagePreferences @Inject constructor(
    private val context: Context
) {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    
    val selectedLanguage: Flow<Language> = context.dataStore.data.map { preferences ->
        val languageCode = preferences[LANGUAGE_KEY] ?: Language.ENGLISH.code
        Language.values().find { it.code == languageCode } ?: Language.ENGLISH
    }
    
    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
    }
}