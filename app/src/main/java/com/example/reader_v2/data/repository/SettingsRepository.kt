package com.example.reader_v2.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.reader_v2.ui.screen.reader.ReaderSettings
import com.example.reader_v2.ui.screen.reader.ReaderTheme
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private object Keys {
            val FONT_SIZE = intPreferencesKey("font_size")
            val THEME = stringPreferencesKey("theme")
            val LINE_HEIGHT = floatPreferencesKey("line_height")
        }

        val readerSettings: Flow<ReaderSettings> =
            dataStore.data.map { prefs ->
                ReaderSettings(
                    fontSize = prefs[Keys.FONT_SIZE] ?: 18,
                    theme = ReaderTheme.valueOf(prefs[Keys.THEME] ?: ReaderTheme.Light.name),
                    lineHeight = prefs[Keys.LINE_HEIGHT] ?: 1.5f,
                )
            }

        suspend fun updateSettings(settings: ReaderSettings) {
            dataStore.edit { prefs ->
                prefs[Keys.FONT_SIZE] = settings.fontSize
                prefs[Keys.THEME] = settings.theme.name
                prefs[Keys.LINE_HEIGHT] = settings.lineHeight
            }
        }
    }
