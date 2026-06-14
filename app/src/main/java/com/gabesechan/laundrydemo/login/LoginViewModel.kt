package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginAPI: LoginAPI
): ViewModel() {

    var phone by mutableStateOf(TextFieldState())
        private set
    var password by mutableStateOf(TextFieldState())
        private set



    private val _showSpinner = MutableStateFlow(false)
    val showSpinner = _showSpinner.asStateFlow()

    private val _errorTextId = MutableStateFlow(0)
    val errorTextId = _errorTextId.asStateFlow()

    val loginEnabled = combine(
        phone.asFlow(),
        password.asFlow(),
        _showSpinner,
    ) { phone, password, showSpinner ->
        !showSpinner && phone.isNotEmpty() && password.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    fun onLoginClicked() {
        _showSpinner.value = true
        _errorTextId.value = 0
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginAPI.login(phone.text.toString(),password.text.toString())
            when (result) {
                is LoginAPI.LoginResult.NetworkError -> {
                    _errorTextId.value = R.string.network_error
                }

                is LoginAPI.LoginResult.LoginFailed -> {
                    _errorTextId.value = R.string.bad_auth
                }

                else -> {
                    _errorTextId.value = 0
                }
            }
            _showSpinner.value = false
        }

    }

}