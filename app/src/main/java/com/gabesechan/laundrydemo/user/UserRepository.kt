package com.gabesechan.laundrydemo.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor( private val datastore: DataStore<Preferences>) {
    private val _current: MutableStateFlow<User> = MutableStateFlow(User.NoUser)
    val current = _current.asStateFlow()

    private val namePreferenceKey = stringPreferencesKey("Name")
    private val idPreferenceKey = stringPreferencesKey("id")
    private val emailPreferenceKey = stringPreferencesKey("email")
    private val phonePreferenceKey = stringPreferencesKey("phone")


    suspend fun initFromDisk() {
        val stored = datastore.data.first()
        val name = stored[namePreferenceKey]
        val id = stored[idPreferenceKey]
        val phone = stored[phonePreferenceKey]
        val email = stored[emailPreferenceKey]
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
            it[idPreferenceKey] = user.id
            it.writeOrRemove(emailPreferenceKey, user.email)
            it.writeOrRemove(phonePreferenceKey, user.phone)
        }
    }

    suspend fun clearUser() {
        _current.value = User.NoUser
        datastore.edit {
            it.remove(namePreferenceKey)
            it.remove(emailPreferenceKey)
            it.remove(idPreferenceKey)
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