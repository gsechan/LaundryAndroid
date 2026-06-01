package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.SelectableDates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WashFoldViewModel @Inject constructor(
    availableTimesServer: AvailableTimesServer,
): ViewModel() {

    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded = _dataLoaded.asStateFlow()

    lateinit var availableTimesResponse: AvailableTimesResponse

    init {
        viewModelScope.launch(Dispatchers.IO) {
            availableTimesResponse = availableTimesServer.availableTimes()
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
}