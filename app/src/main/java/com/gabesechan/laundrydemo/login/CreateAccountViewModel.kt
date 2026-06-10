package com.gabesechan.laundrydemo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    val loginAPI: LoginAPI
): ViewModel() {

    private val _createEnabled = MutableStateFlow<Boolean>(true)
    val createEnabled = _createEnabled.asStateFlow()

    fun createAccountClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            loginAPI.createAccount()
        }
    }

}