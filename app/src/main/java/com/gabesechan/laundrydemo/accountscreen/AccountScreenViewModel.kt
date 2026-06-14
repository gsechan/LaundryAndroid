package com.gabesechan.laundrydemo.accountscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.user.UserRepository
import com.gabesechan.laundrydemo.user.UserServer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    private val loginAPI: LoginAPI,
    private val userRepository: UserRepository,
    private val userServer: UserServer,
): ViewModel() {

    val user = userRepository.current

    var networkError = false

    fun onLogoutClicked(){
        viewModelScope.launch(Dispatchers.IO) { loginAPI.logout() }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userServer.deleteAddress(address.id).process()
                val user = userRepository.current.value.copy(
                    addresses = userRepository.current.value.addresses.filter { it.id != address.id }
                )
                userRepository.setUser(user)
            }
            catch (_: IOException) {
                networkError = true
            }
        }
    }
}