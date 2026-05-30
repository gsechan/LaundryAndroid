package com.gabesechan.laundrydemo.account

import androidx.compose.ui.graphics.vector.Path
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Scope

class UserRepository @Inject constructor( private val datastore: DataStore<Preferences>) {
    private val _current: MutableStateFlow<User> = MutableStateFlow(User.NoUser)
    val current = _current.asStateFlow()

    private val namePreferenceKey = stringPreferencesKey("Name")
    private val idPrefenceKey = stringPreferencesKey("id")
    private val emailPreferkeceKey = stringPreferencesKey("email")
    private val phonePreferenceKey = stringPreferencesKey("phone")


    suspend fun initFromDisk() {
        val stored = datastore.data.first()
        val name = stored[namePreferenceKey]
        val id = stored[idPrefenceKey]
        val phone = stored[phonePreferenceKey]
        val email = stored[emailPreferkeceKey]
        if(name != null && id != null) {
            _current.value = User.RealUser(id, name, email, phone)
        }
        else {
            _current.value = User.NoUser
        }
    }

    suspend fun setUser(user: User) {
        if(user is User.NoUser) {
            throw RuntimeException("Cannot use login to logout")
        }
        _current.value = user
        datastore.edit {
            it[namePreferenceKey] = user.name
            it[idPrefenceKey] = user.id
            it.writeOrRemove(emailPreferkeceKey, user.email)
            it.writeOrRemove(phonePreferenceKey, user.phone)
        }
    }

    suspend fun clearUser() {
        _current.value = User.NoUser
        datastore.edit {
            it.remove(namePreferenceKey)
            it.remove(emailPreferkeceKey)
            it.remove(idPrefenceKey)
            it.remove(phonePreferenceKey)
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