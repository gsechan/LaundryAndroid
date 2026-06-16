package com.gabesechan.laundrydemo.accountscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.login.UserRepository
import com.gabesechan.laundrydemo.user.UserServer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _deleteAddressRunning = MutableStateFlow(false)
    val deleteAddressRunning = _deleteAddressRunning.asStateFlow()

    fun onLogoutClicked(){
        viewModelScope.launch(Dispatchers.IO) { loginAPI.logout() }
    }

    fun deleteAddress(address: Address) {
        _deleteAddressRunning.value = true
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
            _deleteAddressRunning.value = false
        }
    }
}