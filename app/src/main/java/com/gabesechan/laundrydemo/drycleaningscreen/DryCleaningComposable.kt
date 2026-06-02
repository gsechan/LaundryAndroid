package com.gabesechan.laundrydemo.drycleaningscreen

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
import com.gabesechan.laundrydemo.ui.widgets.NumericDropdownMenu
import com.gabesechan.laundrydemo.user.Address
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun DryCleaningComposable(viewModel: DryCleaningViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val isBooked by viewModel.isBooked.collectAsState()
    val pickupDateValues by viewModel.pickupDateValues.collectAsState()
    val dropoffDateValues by viewModel.dropoffDateValues.collectAsState()
    val selectedAddressIndex by viewModel.selectedAddressIndex.collectAsState()
    val addresses by viewModel.addresses.collectAsState(emptyList())
    val itemCounts by viewModel.itemCounts.collectAsState()

    if(isBooked) {
        Text(stringResource(R.string.order_booked))
    }
    else if(isLoaded) {

        DryCleaningComposableInner(
            addresses,
            selectedAddressIndex,
            viewModel::selectAddress,
            pickupDateValues,
            DateTimePickerCallbacks(
                viewModel::setPickupDate, viewModel::setPickupTime
            ),
            dropoffDateValues,
            DateTimePickerCallbacks(
                viewModel::setDropoffDate, viewModel::setDropoffTime
            ),
            viewModel.washPrice(),
            viewModel::book,
            itemCounts,
            viewModel::onCountChanged,
            viewModel.getPrices()
        )
    }
}

@Composable
fun DryCleaningComposableInner(
    addresses: List<Address>,
    selectedAddress: Int,
    onAddressSelected: (Int)->Unit,
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    washFoldPrice: Int,
    onBook: ()->Unit,
    itemCounts: Map<String, Int>,
    onCountChanged: (String, Int)->Unit,
    prices: Map<String, Int>
) {
    Column(Modifier.fillMaxHeight()) {
        Text(stringResource(R.string.dry_clean))
        NumericDropdownMenu("Shirts", 0,10, 0, { onCountChanged("shirts", it) })
        NumericDropdownMenu("Pants", 0,10, 0, { onCountChanged("pants", it)})
        Spacer(Modifier.height(12.dp))
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
            val shirtCost = itemCounts["shirts"]!! * prices["shirts"]!!
            val pantsCost = itemCounts["pants"]!! * prices["pants"]!!
            val price = BigDecimal(shirtCost + pantsCost).movePointLeft(2)
            val formatter = NumberFormat.getCurrencyInstance()
            val totalPrice = price.times(BigDecimal(8))
            Text(
                stringResource(
                    R.string.total_price,
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

