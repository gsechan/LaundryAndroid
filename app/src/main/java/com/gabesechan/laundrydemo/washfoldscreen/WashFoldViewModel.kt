package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.SelectableDates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WashFoldViewModel @Inject constructor(
    laundromatInfoServer: LaundromatInfoServer,
): ViewModel() {

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

    fun washPrice(): Int = pricesResponse.washFold


    fun availablePickups(): List<AvailableDateTime> = availableTimesResponse.pickup
    fun availableDropoffs(): List<AvailableDateTime> = availableTimesResponse.delivery
    fun minTimeBetweenPickupAndDelivery(): Long = availableTimesResponse.minTimeBetweenPickupAndDelivery

    fun getSelectablePickupDates(): SelectableDates {
        if(dataLoaded.value == false) {
            return DatePickerDefaults.AllDates
        }
        return object: SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Convert milliseconds to local date
                availablePickups().forEach {
                    if(it.date == utcTimeMillis) {
                        return@isSelectableDate true
                    }
                }
                return false
            }

            override fun isSelectableYear(year: Int): Boolean {
                availablePickups().forEach {
                    if(Instant.ofEpochMilli(it.date)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate().year == year) {
                            return@isSelectableYear true
                    }
                }
                return false
            }

        }
    }

    fun getSelectableDropoffDates(): SelectableDates {
        if(dataLoaded.value == false) {
            return DatePickerDefaults.AllDates
        }
        return object: SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Convert milliseconds to local date
                availableDropoffs().forEach {
                    if(it.date == utcTimeMillis) {
                        return@isSelectableDate true
                    }
                }
                return false
            }

            override fun isSelectableYear(year: Int): Boolean {
                availableDropoffs().forEach {
                    if(Instant.ofEpochMilli(it.date)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate().year == year) {
                        return@isSelectableYear true
                    }
                }
                return false
            }

        }
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