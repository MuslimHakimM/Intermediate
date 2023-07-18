package com.latihan.intermediate1.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.latihan.intermediate1.LoginPreference
import com.latihan.intermediate1.data.model.data.Repository
import com.latihan.intermediate1.data.remote.ApiConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

object Injection {
    fun provideRepository(context: Context): Repository {
        val preferences = LoginPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository(preferences, apiService)
    }


}