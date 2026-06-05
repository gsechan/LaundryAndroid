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
import com.gabesechan.laundrydemo.ui.widgets.AddressPicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerCallbacks
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import com.gabesechan.laundrydemo.user.Address
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun WashFoldScreen(viewModel: WashFoldViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val isBooked by viewModel.isBooked.collectAsState()
    val pickupDateValues by viewModel.pickupDateValues.collectAsState()
    val dropoffDateValues by viewModel.dropoffDateValues.collectAsState()
    val selectedAddressIndex by viewModel.selectedAddressIndex.collectAsState()
    val addresses by viewModel.addresses.collectAsState(emptyList())

    if(isBooked) {
        Text(stringResource(R.string.order_booked))
    }
    else if(isLoaded) {

        WashFoldScreenInner(
            addresses,
            selectedAddressIndex,
            viewModel::selectAddress,
            pickupDateValues,
            DateTimePickerCallbacks(
                viewModel::setPickupDate,viewModel::setPickupTime
            ),
            dropoffDateValues,
        DateTimePickerCallbacks(
            viewModel::setDropoffDate, viewModel::setDropoffTime
             ),
            viewModel.washPrice(),
            viewModel.avgWeight(),
            viewModel::book
        )
    }
}

@Composable
fun WashFoldScreenInner(
    addresses: List<Address>,
    selectedAddress: Int,
    onAddressSelected: (Int)->Unit,
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    washFoldPrice: BigDecimal,
    avgWeight: BigDecimal,
    onBook: ()->Unit
) {
    Column(Modifier.fillMaxHeight()) {
        Text(stringResource(R.string.wash_fold))

        AddressPicker(addresses, selectedAddress, onAddressSelected)


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
            val formatter = NumberFormat.getCurrencyInstance()
            val totalPrice = washFoldPrice.times(avgWeight)
            Text(
                stringResource(
                    R.string.expected_wash_price,
                    formatter.format(washFoldPrice),
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

