package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class UserRepository @Inject constructor() {
    private val _current: MutableStateFlow<User> = MutableStateFlow(User.Companion.NoUser)
    val current = _current.asStateFlow()

    fun setUser(user: User) {
        if(user == User.Companion.NoUser) {
            throw RuntimeException("Cannot use login to logout")
        }
        _current.value = user
    }

    fun clearUser() {
        _current.value = User.Companion.NoUser
    }
}