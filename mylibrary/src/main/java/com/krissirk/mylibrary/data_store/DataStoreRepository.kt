package com.krissirk.mylibrary.data_store

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logger_data_pref")

class DataStoreRepository(context: Context) {

    private object PreferencesKey {
        val logList = stringPreferencesKey(name = "logList")
    }

    private val dataStore = context.dataStore

    suspend fun saveLogList(logList: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.logList] = logList
            Log.d("saveIsUseBiometric success ", logList)
        }
    }

    fun readLogList(): Flow<String> {
        return dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val shouldBenefitDetailHint = preferences[PreferencesKey.logList] ?: ""
                shouldBenefitDetailHint
            }
    }
}