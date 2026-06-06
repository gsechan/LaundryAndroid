package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val addresses by viewModel.addresses.collectAsState(emptyList())

    if(isBooked) {
        Text(stringResource(R.string.order_booked))
    }
    else if(isLoaded) {

        WashFoldScreenInner(
            addresses,
            selectedAddress,
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
    selectedAddress: Address,
    onAddressSelected: (Address)->Unit,
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    washFoldPrice: BigDecimal,
    avgWeight: BigDecimal,
    onBook: ()->Unit
) {
    Column(
        Modifier.fillMaxHeight().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val formatter = NumberFormat.getCurrencyInstance()
        val totalPrice = washFoldPrice.times(avgWeight)
        Text(
            stringResource(
                R.string.expected_wash_price,
                formatter.format(washFoldPrice),
                formatter.format(totalPrice)
            )
        )

        AddressPicker(addresses, selectedAddress, onAddressSelected)


        DateTimePicker(
            label =stringResource(R.string.pickup_select),
            dateTimeValues = pickup,
            callbacks = pickupCallbacks
        )
        if(pickup.curSelectedTime != null) {
            DateTimePicker(
                label = stringResource(R.string.dropoff_select),
                dateTimeValues = dropoff,
                callbacks = dropoffCallbacks
            )
        }
        Button(onBook, enabled = dropoff.curSelectedTime!= null, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {
            Text(stringResource(R.string.book_now), textAlign = TextAlign.Center)
        }
    }
}

