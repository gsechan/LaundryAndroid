package com.gabesechan.laundrydemo.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserRepository @Inject constructor( private val datastore: DataStore<Preferences>) {
    private val _current: MutableStateFlow<User> = MutableStateFlow(User.NoUser)
    val current = _current.asStateFlow()

   var authToken = ""
       private set


    private val userKey = stringPreferencesKey("json")
    private val tokenKey = stringPreferencesKey("token")


    suspend fun initFromDisk() {
        val stored = datastore.data.first()
        val json = stored[userKey]
        val token = stored[tokenKey]
        if(json != null && token!= null) {
            val user = Json.decodeFromString<User>(json)
            _current.value = user
            authToken = token
        }
        else {
            _current.value = User.NoUser
            authToken = ""
        }
    }

    suspend fun setUser(user: User, session: String) {
        if(user == User.NoUser) {
            throw RuntimeException("Cannot use login to logout")
        }
        _current.value = user
        authToken = session
        datastore.edit {
            it[userKey] = Json.encodeToString(user)
            it[tokenKey] = session
        }
    }

    suspend fun clearUser() {
        _current.value = User.NoUser

        datastore.edit {
            it.remove(userKey)
            it.remove(tokenKey)
        }
    }

}

fun <T> MutablePreferences.writeOrRemove(key: Preferences.Key<T>, value: T?) {
    if(value != null) {
        this[key] = value
    }
    else {
        remove(key)
    }
}