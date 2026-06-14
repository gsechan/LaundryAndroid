package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.models.Address
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
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val addressId: String = savedStateHandle.get<String>("id") ?: "new"

    private val existingAddress: Address? = userRepository.current.value.addresses.find { it.id == addressId }

    val isEditing = existingAddress != null

    private val _addRunning = MutableStateFlow(false)
    val addRunning = _addRunning.asStateFlow()

    var networkError = false

    var street1 by mutableStateOf(TextFieldState(existingAddress?.street1 ?: ""))
        private set
    var street2 by mutableStateOf(TextFieldState(existingAddress?.street2 ?: ""))
        private set
    var country by mutableStateOf(TextFieldState(existingAddress?.country ?: ""))
        private set
    var city by mutableStateOf(TextFieldState(existingAddress?.city ?: ""))
        private set
    var state by mutableStateOf(TextFieldState(existingAddress?.state ?: ""))
        private set
    var postcode by mutableStateOf(TextFieldState(existingAddress?.postcode ?: ""))
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
            val newAddress = Address(
                if (addressId == "new") "" else addressId,
                street1.text.toString(),
                street2.text.toString(),
                city.text.toString(),
                state.text.toString(),
                country.text.toString(),
                postcode.text.toString(),
            )

            try {
                if (addressId == "new") {
                    val response = userServer.addAddress(PostAddressRequest(newAddress)).process()
                    val user = userRepository.current.value.copy(
                        addresses = userRepository.current.value.addresses + response.address
                    )
                    userRepository.setUser(user)
                } else {
                    val response = userServer.updateAddress(addressId, PatchAddressRequest(newAddress)).process()
                    val user = userRepository.current.value.copy(
                        addresses = userRepository.current.value.addresses.map {
                            if (it.id == addressId) response.address else it
                        }
                    )
                    userRepository.setUser(user)
                }
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