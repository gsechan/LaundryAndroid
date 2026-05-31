package com.gabesechan.laundrydemo.accountscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    private val loginAPI: LoginAPI,
    private val userRepository: UserRepository,
): ViewModel() {

    val user = userRepository.current

    fun onLogoutClicked(){
        viewModelScope.launch(Dispatchers.IO) { loginAPI.logout() }
    }
}