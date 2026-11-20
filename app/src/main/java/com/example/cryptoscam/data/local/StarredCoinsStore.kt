package com.example.cryptoscam.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class StarredCoinsStore(private val context: Context) {
    private val key = stringSetPreferencesKey("starred_ids")

    val starredIds: Flow<Set<String>> =
        context.dataStore.data.map { it[key] ?: emptySet() }

    suspend fun toggle(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = if (id in current) current - id else current + id
        }
    }
}