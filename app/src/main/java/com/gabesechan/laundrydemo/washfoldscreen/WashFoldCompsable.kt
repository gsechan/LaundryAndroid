package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.DateTimePicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerCallbacks
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import java.math.BigDecimal
import java.text.NumberFormat

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
            DateTimePickerValues(
                viewModel.getSelectablePickupDates(),
                pickupDate,
                viewModel.getPickupTimesForCurrentDate(),
                pickupTime
            ),
            DateTimePickerCallbacks(
                viewModel::setPickupDate,viewModel::setPickupTime
            ),
            DateTimePickerValues(
                viewModel.getSelectableDropoffDates(),
                dropoffDate,
                viewModel.getDropOffTimesForCurrentDate(),
                dropoffTime
            ),
        DateTimePickerCallbacks(
            viewModel::setDropoffDate, viewModel::setDropoffTime
             ),
            viewModel.washPrice(),
            viewModel::book
        )
    }
}

@Composable
fun WashFoldScreenInner(
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    washFoldPrice: Int,
    onBook: ()->Unit
) {
    Column(Modifier.fillMaxHeight()) {
        Text(
            text = "Wash and Fold",
        )
        DateTimePicker(
            stringResource(R.string.pickup_select),
            pickup,
            pickupCallbacks
        )
        Spacer(Modifier.height(12.dp))
        if(pickup.curSelectedTime != null) {
            DateTimePicker(
                stringResource(R.string.dropoff_select),
                dropoff,
                dropoffCallbacks
            )
        }
        if(dropoff.curSelectedTime != null) {
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

