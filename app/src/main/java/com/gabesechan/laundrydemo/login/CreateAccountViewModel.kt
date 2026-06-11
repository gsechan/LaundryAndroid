package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    val loginAPI: LoginAPI
): ViewModel() {

    private val _createRunning = MutableStateFlow(false)
    val createRunning = _createRunning.asStateFlow()

    var name by mutableStateOf(TextFieldState())
        private set

    var phone by mutableStateOf(TextFieldState())
        private set

    var email by mutableStateOf(TextFieldState())
        private set

    var password1 by mutableStateOf(TextFieldState())
        private set
    var password2 by mutableStateOf(TextFieldState())
        private set

    val createEnabled = combine(
        _createRunning,
        name.asFlow(),
        phone.asFlow(),
        email.asFlow(),
        password1.asFlow(),
        password2.asFlow(),
    ) { running: Boolean, name: String, phone:String, email:String, password1:String, password2:String ->
        !running && name.length >=2 && phone.length >=5 && email.length >= 3 && password1.length >=8 &&
                password1 == password2
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    fun createAccountClicked() {
        _createRunning.value = true
        viewModelScope.launch(Dispatchers.IO) {
            loginAPI.createAccount(
                name.text.toString(),
                password1.text.toString(),
                phone.text.toString(),
                email.text.toString()
            )
            _createRunning.value = false
        }
    }

}

fun TextFieldState.asFlow(): Flow<String> = snapshotFlow { text.toString() }

fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(flow1, flow2, flow3, flow4, flow5, flow6) { values ->
    @Suppress("UNCHECKED_CAST")
    transform(
        values[0] as T1,
        values[1] as T2,
        values[2] as T3,
        values[3] as T4,
        values[4] as T5,
        values[5] as T6
    )
}