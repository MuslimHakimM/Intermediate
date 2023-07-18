package com.latihan.intermediate1

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.latihan.intermediate1.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                preferences[NAME_KEY] ?:"",
                preferences[TOKEN] ?:"",
                preferences[USER_ID_KEY] ?:"",
                preferences[LOGIN_STATE] ?: false
            )
        }
    }

    suspend fun setUser(user: UserSession) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[TOKEN] = user.token
            preferences[USER_ID_KEY] = user.userId
            preferences[LOGIN_STATE] = user.isLogin
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[LOGIN_STATE] = false
            preferences[TOKEN] = ""
            preferences[NAME_KEY] = ""
            preferences[USER_ID_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("email")
        private val USER_ID_KEY = stringPreferencesKey("password")
        private val LOGIN_STATE = booleanPreferencesKey("state")


        fun getInstance(dataStore: DataStore<Preferences>): LoginPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }


    }
}