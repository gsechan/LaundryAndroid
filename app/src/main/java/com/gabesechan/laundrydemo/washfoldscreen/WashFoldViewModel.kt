package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.material3.DatePickerDefaults.AllDates
import androidx.compose.material3.SelectableDates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.laundromatinfo.AvailableDateTime
import com.gabesechan.laundrydemo.laundromatinfo.AvailableTimesResponse
import com.gabesechan.laundrydemo.laundromatinfo.ItemsResponse
import com.gabesechan.laundrydemo.laundromatinfo.JSONItem
import com.gabesechan.laundrydemo.laundromatinfo.LaundromatInfoServer
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import com.gabesechan.laundrydemo.laundromatinfo.WashFoldResponse
import com.gabesechan.laundrydemo.orders.OrdersServer
import com.gabesechan.laundrydemo.orders.PostOrder
import com.gabesechan.laundrydemo.orders.PostOrderLine
import com.gabesechan.laundrydemo.orders.PostOrderRequest
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import com.gabesechan.laundrydemo.user.Address
import com.gabesechan.laundrydemo.user.UserRepository
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
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WashFoldViewModel @Inject constructor(
    private val laundromatInfoServer: LaundromatInfoServer,
    userRepository: UserRepository,
    private val orderServer: OrdersServer,
): ViewModel() {

    val addresses = userRepository.current.map { it.addresses }
    var networkError = false

    private val _selectedAddress = MutableStateFlow(userRepository.current.value.addresses.getOrNull(0))
    val selectedAddress = _selectedAddress.asStateFlow()


    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded = _dataLoaded.asStateFlow()

    private lateinit var availableTimesResponse: AvailableTimesResponse
    private lateinit var pricesResponse: ItemsResponse
    private lateinit var items: List<JSONItem>


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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                availableTimesResponse = laundromatInfoServer.availableTimes().process()
                pricesResponse = laundromatInfoServer.items().process()
                items = laundromatInfoServer.items().process().items.filter { it.itemType == "WASH_AND_FOLD" }
                _pickupDateValues.value = _pickupDateValues.value.copy(
                    selectableDates = SelectableDeliveryDates(availableTimesResponse.pickup, 0)
                )
            }
            catch (ex: IOException) {
                networkError = true
            }
            _dataLoaded.value = true
        }
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun washPrice(): BigDecimal = BigDecimal(items[0].price)

    private class SelectableDeliveryDates(
        dates: List<AvailableDateTime>,
        earliestDay: Long
    ): SelectableDates {
        val allowedYears = mutableSetOf<Int>()
        val allowedDates = mutableSetOf<Long>()

        init {
            dates.forEach {
                if(it.date >= earliestDay) {
                    allowedYears.add(
                        Instant.ofEpochMilli(it.date)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate().year
                    )
                    allowedDates.add(it.date)
                }
            }
        }

        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return allowedDates.contains(utcTimeMillis)
        }

        override fun isSelectableYear(year: Int): Boolean {
            return allowedYears.contains(year)
        }

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
                val result = orderServer.postOrder(
                    PostOrderRequest(
                        PostOrder(
                            listOf(
                                PostOrderLine(items[0].id, null, "WASH_AND_FOLD"),
                            ),
                            _pickupDateValues.value.toUtcTime(),
                            _dropoffDateValues.value.toUtcTime(),
                            _selectedAddress.value!!.id,
                            _selectedAddress.value!!.id
                        )
                    )
                )
                _isBooked.value = true
            }
            catch (ex: IOException) {
                networkError = true
            }
            _orderPosting.value = false
        }
    }

    val bookEnabled = combine(_dropoffDateValues, _orderPosting, _selectedAddress) {
            dropoff, posting, address ->
        dropoff.curSelectedTime != null && !posting && address != null
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
}