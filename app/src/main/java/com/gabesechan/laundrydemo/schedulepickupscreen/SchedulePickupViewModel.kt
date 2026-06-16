package com.gabesechan.laundrydemo.schedulepickupscreen

import androidx.compose.material3.DatePickerDefaults.AllDates
import com.gabesechan.laundrydemo.laundromatinfo.AvailableTimesResponse
import com.gabesechan.laundrydemo.laundromatinfo.LaundromatInfoServer
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.models.Item
import com.gabesechan.laundrydemo.orders.OrdersServer
import com.gabesechan.laundrydemo.orders.PostOrder
import com.gabesechan.laundrydemo.orders.PostOrderLine
import com.gabesechan.laundrydemo.orders.PostOrderRequest
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import com.gabesechan.laundrydemo.ui.widgets.SelectableDeliveryDates
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class SchedulePickupViewModel @Inject constructor(
    private val laundromatInfoServer: LaundromatInfoServer,
    userRepository: UserRepository,
    private val orderServer: OrdersServer,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val itemType: String = savedStateHandle.get<String>("itemType") ?: "DRY_CLEANING"

    val addresses = userRepository.current.map { it.addresses }

    var dataError: Boolean = false

    private val _selectedAddress = MutableStateFlow(userRepository.current.value.addresses.getOrNull(0))
    val selectedAddress = _selectedAddress.asStateFlow()


    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded = _dataLoaded.asStateFlow()

    private lateinit var availableTimesResponse: AvailableTimesResponse
    var items: List<Item> = emptyList()
        private set


    private val _pickupDateValues = MutableStateFlow(
        DateTimePickerValues(
            AllDates,
            null,
            emptyList(),
            null
        )
    )
    val pickupDateValues = _pickupDateValues.asStateFlow()

    private val _dropoffDateValues = MutableStateFlow(
        DateTimePickerValues(
            AllDates,
            null,
            emptyList(),
            null
        )
    )
    val dropoffDateValues = _dropoffDateValues.asStateFlow()

    private val _isBooked = MutableStateFlow(false)
    val isBooked = _isBooked.asStateFlow()

    private val _itemCounts = MutableStateFlow(mapOf<String, Int>())
    val itemCounts = _itemCounts.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                availableTimesResponse = laundromatInfoServer.availableTimes().process()
                items = laundromatInfoServer.items().process().items.filter { it.itemType == itemType }
                val initCounts = items.associate {
                    it.id to 0
                }
                _itemCounts.value = initCounts
                _pickupDateValues.value = _pickupDateValues.value.copy(
                    selectableDates = SelectableDeliveryDates(availableTimesResponse.pickup, 0)
                )
            }
            catch (_: IOException) {
                dataError = true
            }
            _dataLoaded.value = true
        }
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun setPickupDate(date: Long?) {
        _pickupDateValues.value = _pickupDateValues.value.copy(
            curSelectedDate = date,
            selectableTimes = availableTimesResponse.pickup.first { it.date == date}.times
        )
        _dropoffDateValues.value = _dropoffDateValues.value.copy(
            selectableDates = AllDates,
            curSelectedDate = null,
            selectableTimes = emptyList(),
            curSelectedTime = null,
        )
    }

    fun setPickupTime(time: TimeRange) {
        _pickupDateValues.value = _pickupDateValues.value.copy(
            curSelectedTime = time,
        )
        val pickupDate = _pickupDateValues.value.curSelectedDate ?: 0
        val earliest = pickupDate + availableTimesResponse.minTimeBetweenPickupAndDelivery
        _dropoffDateValues.value = _dropoffDateValues.value.copy(
            selectableDates = SelectableDeliveryDates(availableTimesResponse.delivery, earliest),
            curSelectedDate = null,
            selectableTimes = emptyList(),
            curSelectedTime = null,
        )
    }

    fun setDropoffDate(date: Long?) {
        _dropoffDateValues.value = _dropoffDateValues.value.copy(
            curSelectedDate = date,
            selectableTimes = availableTimesResponse.delivery.first { it.date == date}.times
        )
    }

    fun setDropoffTime(time: TimeRange) {
        _dropoffDateValues.value = _dropoffDateValues.value.copy(
            curSelectedTime = time
        )
    }

    private val _orderPosting = MutableStateFlow(false)
    val showBookingSpinner = _orderPosting.asStateFlow()

    fun book() {
        _orderPosting.value = true
        viewModelScope.launch {
            try {
                val orderLines = if (itemType == "WASH_AND_FOLD") {
                    items.map {
                        PostOrderLine(it.id, null)
                    }
                } else {
                    _itemCounts.value.filter { it.value > 0 }.map { entry ->
                        PostOrderLine(entry.key, entry.value.toString())
                    }
                }
                orderServer.postOrder(
                    PostOrderRequest(
                        PostOrder(
                            orderLines,
                            _pickupDateValues.value.toUtcTime(),
                            _dropoffDateValues.value.toUtcTime(),
                            _selectedAddress.value!!.id,
                            _selectedAddress.value!!.id
                        )
                    )
                )
                _isBooked.value = true
            }
            catch(_: IOException) {
                dataError = true
            }
            _orderPosting.value = false
        }
    }

    fun onCountChanged(item:String, value: Int) {
        val counts = _itemCounts.value.toMutableMap()
        counts[item] = value
        _itemCounts.value = counts
    }

    val bookEnabled = combine(_dropoffDateValues, _itemCounts, _orderPosting, _selectedAddress, _pickupDateValues) {
        dropoff, counts, posting, address, pickup ->
        val enabledForItemType = itemType == "WASH_AND_FOLD" || (itemType == "DRY_CLEANING" && counts.any{ entry-> entry.value != 0})
        pickup.curSelectedDate != null && pickup.curSelectedTime!= null &&
            dropoff.curSelectedDate != null && dropoff.curSelectedTime != null &&
            enabledForItemType && !posting && address != null
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
}