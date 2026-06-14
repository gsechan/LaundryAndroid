package com.gabesechan.laundrydemo.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenStorage @Inject constructor(private val datastore: DataStore<Preferences>) {

    var authToken = ""
        private set

    private val tokenKey = stringPreferencesKey("token")

    suspend fun initFromDisk(): String? {
        val stored = datastore.data.first()
        return stored[tokenKey]
    }

    suspend fun setToken(token: String) {
        authToken = token
        datastore.edit {
            it[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        authToken = ""
        datastore.edit {
            it.remove(tokenKey)
        }
    }
}