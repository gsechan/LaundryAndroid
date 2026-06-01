package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.DatePickerTextfield
import com.gabesechan.laundrydemo.ui.widgets.DisplayTimes
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun WashFoldScreen(viewModel: WashFoldViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val isBooked by viewModel.isBooked.collectAsState()
    val pickupDate by viewModel.pickupDate.collectAsState(null)
    val pickupTime by viewModel.pickupTime.collectAsState(null)
    val dropoffDate by viewModel.dropOffDate.collectAsState(null)
    val dropoffTime by viewModel.dropOffTime.collectAsState(null)

    if(isBooked) {
        Text(stringResource(R.string.order_booked))
    }
    else if(isLoaded) {
        WashFoldScreenInner(
            viewModel.getSelectablePickupDates(),
            viewModel.getPickupTimesForCurrentDate(),
            viewModel.getSelectableDropoffDates(),
            viewModel.getDropOffTimesForCurrentDate(),
            viewModel::setPickupDate,
            viewModel::setPickupTime,
            viewModel::setDropoffDate,
            viewModel::setDropoffTime,
            pickupDate,
            pickupTime,
            dropoffDate,
            dropoffTime,
            viewModel.washPrice(),
            viewModel::book
        )
    }
}

@Composable
fun WashFoldScreenInner(
    pickupDates: SelectableDates,
    pickupTimes: List<TimeRange>,
    dropoffDates: SelectableDates,
    dropoffTimes: List<TimeRange>,
    pickupDateSelected: (Long?)-> Unit,
    pickupTimeSelected: (TimeRange)->Unit,
    dropoffDateSelected: (Long?)-> Unit,
    dropoffTimeSelected: (TimeRange)->Unit,
    selectedPickupDate: Long?,
    selectedPickupTime: TimeRange?,
    selectedDropoffDate: Long?,
    selectedDropoffTime: TimeRange?,
    washFoldPrice: Int,
    onBook: ()->Unit
) {
    Column(Modifier.fillMaxHeight()) {
        Text(
            text = "Wash and Fold",
        )
        DateTimePicker(
            stringResource(R.string.pickup_select),
            pickupDates,
            pickupTimes,
            pickupDateSelected,
            pickupTimeSelected,
            selectedPickupDate,
            selectedPickupTime
        )
        Spacer(Modifier.height(12.dp))
        if(selectedPickupTime != null) {
            DateTimePicker(
                stringResource(R.string.dropoff_select),
                dropoffDates,
                dropoffTimes,
                dropoffDateSelected,
                dropoffTimeSelected,
                selectedDropoffDate,
                selectedDropoffTime
            )
        }
        if(selectedDropoffTime != null) {
            Spacer(Modifier.height(12.dp))
            val price = BigDecimal(washFoldPrice).movePointLeft(2)
            val formatter = NumberFormat.getCurrencyInstance()
            val totalPrice = price.times(BigDecimal(8))
            Text(
                stringResource(
                    R.string.expected_wash_price,
                    formatter.format(price),
                    formatter.format(totalPrice)
                    )
            )
            Spacer(Modifier.height(12.dp))
            Button(onBook) {
                Text(stringResource(R.string.book_now))
            }
        }
    }
}

@Composable
fun DateTimePicker(
    datePickerLabel: String,
    pickupDates: SelectableDates,
    pickupTimes: List<TimeRange>,
    pickupDateSelected: (Long?)-> Unit,
    pickupTimeSelected: (TimeRange)->Unit,
    selectedPickupDate: Long?,
    selectedPickupTime: TimeRange?,
) {
    DatePickerTextfield(
        datePickerLabel,
        pickupDates,
        pickupDateSelected
    )
    if(selectedPickupDate != null) {
        Spacer(Modifier.height(12.dp))
        DisplayTimes(pickupTimes, selectedPickupTime, pickupTimeSelected)
    }

}


