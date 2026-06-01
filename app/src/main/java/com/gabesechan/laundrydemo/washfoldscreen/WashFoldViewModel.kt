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

    private var pickupTimesForDateSelected = emptyList<TimeRange>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            availableTimesResponse = laundromatInfoServer.availableTimes()
            pricesResponse = laundromatInfoServer.prices()
            _dataLoaded.value = true
        }
    }

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

    fun getPickupTimesForCurrentDate(): List<TimeRange> {
        return pickupTimesForDateSelected
    }

    fun setPickupDate(date: Long?) {
        pickupTimesForDateSelected = availablePickups().first { it.date == date}.times
        _pickupDate.value = date
        _pickupTime.value = null
    }

    fun setPickupTime(time: TimeRange) {
        _pickupTime.value = time
    }
}