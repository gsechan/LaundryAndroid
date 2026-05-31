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

    private val jsonKey = stringPreferencesKey("json")


    suspend fun initFromDisk() {
        val stored = datastore.data.first()
        val json = stored[jsonKey]
        if(json != null) {
            val user = Json.decodeFromString<User>(json)
            _current.value = user
        }
        else {
            _current.value = User.NoUser
        }
    }

    suspend fun setUser(user: User) {
        if(user == User.NoUser) {
            throw RuntimeException("Cannot use login to logout")
        }
        _current.value = user
        datastore.edit {
            it[jsonKey] = Json.encodeToString(user)
        }
    }

    suspend fun clearUser() {
        _current.value = User.NoUser
        datastore.edit {
            it.remove(jsonKey)
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