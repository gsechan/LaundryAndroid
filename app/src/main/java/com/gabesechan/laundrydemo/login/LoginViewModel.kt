package com.gabesechan.laundrydemo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginAPI: LoginAPI
): ViewModel() {

    private val _loginButtonEnabled = MutableStateFlow(true)
    val loginButtonEnabled = _loginButtonEnabled.asStateFlow()

    private val _showSpinner = MutableStateFlow(false)
    val showSpinner = _showSpinner.asStateFlow()

    private val _errorTextId = MutableStateFlow<Int>(0)
    val errorTextId = _errorTextId.asStateFlow()

    fun onLoginClicked() {
        _loginButtonEnabled.value = false
        _showSpinner.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginAPI.login("Gabe","pw")
            if(result is LoginAPI.LoginResult.NetworkError) {
                _errorTextId.value = R.string.network_error
            }
            else if(result is LoginAPI.LoginResult.LoginFailed) {
                _errorTextId.value = R.string.bad_auth
            }
            else {
                _errorTextId.value = 0
            }
            _loginButtonEnabled.value = true
            _showSpinner.value = true
        }

    }


}