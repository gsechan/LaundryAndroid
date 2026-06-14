package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.login.LoginAddress
import com.gabesechan.laundrydemo.login.asFlow
import com.gabesechan.laundrydemo.login.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val userServer: UserServer,
    private val userRepository: UserRepository
): ViewModel() {

    private val _addRunning = MutableStateFlow(false)
    val addRunning = _addRunning.asStateFlow()

    var networkError = false

    var street1 by mutableStateOf(TextFieldState())
        private set
    var street2 by mutableStateOf(TextFieldState())
        private set
    var country by mutableStateOf(TextFieldState())
        private set
    var city by mutableStateOf(TextFieldState())
        private set
    var state by mutableStateOf(TextFieldState())
        private set
    var postcode by mutableStateOf(TextFieldState())
        private set

    val createEnabled = combine(
        _addRunning,
        street1.asFlow(),
        country.asFlow(),
        city.asFlow(),
        state.asFlow(),
        postcode.asFlow(),

        ) { running: Boolean, street1: String, country:String, city:String, state:String, postcode:String ->
        !running && street1.isNotEmpty() && country.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() &&
                postcode.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun addAccountClicked() {
        _addRunning.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val request = PostAddressRequest(
                LoginAddress(
                    "",
                street1.text.toString(),
                street2.text.toString(),
                    city.text.toString(),
                    state.text.toString(),
                    country.text.toString(),
                    postcode.text.toString(),
                )
            )
            try {

                val response = userServer.addAddress(request).process()
                val address = Address(
                    response.address.id,
                    response.address.street1,
                    response.address.street2,
                    response.address.city,
                    response.address.state,
                    response.address.country,
                    response.address.postcode
                )
                val user =userRepository.current.value.copy(
                    addresses = userRepository.current.value.addresses + address
                )
                userRepository.setUser(user, userRepository.authToken)
                _navEvents.emit(Unit)
            }
            catch (_: IOException) {
                networkError = true
            }
            _addRunning.value = false
        }
    }


    private val _navEvents = MutableSharedFlow<Unit>(1)
    val navEvent = _navEvents.asSharedFlow()
}