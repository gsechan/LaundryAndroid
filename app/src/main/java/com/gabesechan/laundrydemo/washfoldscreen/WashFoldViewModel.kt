package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.SelectableDates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WashFoldViewModel @Inject constructor(
    laundromatInfoServer: LaundromatInfoServer,
    userRepository: UserRepository
): ViewModel() {

    val addresses = userRepository.current.map { it.addresses }

    private val _selectedAddressIndex = MutableStateFlow(0)
    val selectedAddressIndex = _selectedAddressIndex.asStateFlow()


    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded = _dataLoaded.asStateFlow()

    lateinit var availableTimesResponse: AvailableTimesResponse
    lateinit var pricesResponse: PricesResponse

    private val _pickupDate = MutableStateFlow<Long?>(null)
    val pickupDate = _pickupDate.asSharedFlow()

    private val _pickupTime = MutableStateFlow<TimeRange?>(null)
    val pickupTime = _pickupTime.asSharedFlow()

    private val _dropOffDate = MutableStateFlow<Long?>(null)
    val dropOffDate = _dropOffDate.asSharedFlow()

    private val _dropOffTime = MutableStateFlow<TimeRange?>(null)
    val dropOffTime = _dropOffTime.asSharedFlow()


    private var pickupTimesForDateSelected = emptyList<TimeRange>()
    private var dropOffTimesForDateSelected = emptyList<TimeRange>()

    private val _isBooked = MutableStateFlow(false)
    val isBooked = _isBooked.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            availableTimesResponse = laundromatInfoServer.availableTimes()
            pricesResponse = laundromatInfoServer.prices()
            _dataLoaded.value = true
        }
    }

    fun selectAddress(index: Int) {
        _selectedAddressIndex.value = index
    }

    fun washPrice(): Int = pricesResponse.washFold


    fun availablePickups(): List<AvailableDateTime> = availableTimesResponse.pickup
    fun availableDropoffs(): List<AvailableDateTime> = availableTimesResponse.delivery
    fun minTimeBetweenPickupAndDelivery(): Long = availableTimesResponse.minTimeBetweenPickupAndDelivery


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
    fun getSelectablePickupDates(): SelectableDates {
        return SelectableDeliveryDates(availablePickups(), 0L)
    }

    fun getSelectableDropoffDates(): SelectableDates {
        val pickupDate = _pickupDate.value ?: 0
        val earliest = pickupDate + minTimeBetweenPickupAndDelivery()
        return SelectableDeliveryDates(availableDropoffs(), earliest)
    }


    fun getPickupTimesForCurrentDate(): List<TimeRange> {
        return pickupTimesForDateSelected
    }
    fun getDropOffTimesForCurrentDate(): List<TimeRange> {
        return pickupTimesForDateSelected
    }

    fun setPickupDate(date: Long?) {
        pickupTimesForDateSelected = availablePickups().first { it.date == date}.times
        _pickupDate.value = date
        _pickupTime.value = null
        _dropOffDate.value = null
        _dropOffTime.value = null
        dropOffTimesForDateSelected = emptyList()

    }

    fun setPickupTime(time: TimeRange) {
        _pickupTime.value = time
    }

    fun setDropoffDate(date: Long?) {
        dropOffTimesForDateSelected = availableDropoffs().first { it.date == date}.times
        _dropOffDate.value = date
        _dropOffTime.value = null
    }

    fun setDropoffTime(time: TimeRange) {
        _dropOffTime.value = time
    }

    fun book() {
        _isBooked.value = true
    }


}