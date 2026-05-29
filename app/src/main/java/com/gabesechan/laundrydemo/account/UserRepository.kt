package com.gabesechan.laundrydemo.account

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {
    private val _current: MutableStateFlow<User> = MutableStateFlow(User.NoUser)
    val current = _current.asStateFlow()

    fun setUser(user: User) {
        if(user is User.NoUser) {
            throw RuntimeException("Cannot use login to logout")
        }
        _current.value = user
    }

    fun clearUser() {
        _current.value = User.NoUser
    }

}