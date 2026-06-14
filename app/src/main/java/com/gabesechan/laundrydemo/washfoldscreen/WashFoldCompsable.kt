package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.AddressPicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerCallbacks
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton
import com.gabesechan.laundrydemo.ui.widgets.convertMillisToDate
import com.gabesechan.laundrydemo.models.Address
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun WashFoldScreen(navController: NavController, viewModel: WashFoldViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val isBooked by viewModel.isBooked.collectAsState()
    val pickupDateValues by viewModel.pickupDateValues.collectAsState()
    val dropoffDateValues by viewModel.dropoffDateValues.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val addresses by viewModel.addresses.collectAsState(emptyList())
    val bookEnabled by viewModel.bookEnabled.collectAsState()
    val showBookingSpinner by viewModel.showBookingSpinner.collectAsState()

    if(isBooked) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.order_booked))
        }
    }
    else if(viewModel.networkError) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.network_error))
        }
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
            viewModel::book,
            bookEnabled,
            showBookingSpinner,
            navController

        )
    }
}

@Composable
fun WashFoldScreenInner(
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressSelected: (Address)->Unit,
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    washFoldPrice: BigDecimal,
    onBook: ()->Unit,
    bookEnabled: Boolean,
    showBookingSpinner: Boolean,
    navController: NavController
) {
    Column(
        Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val formatter = NumberFormat.getCurrencyInstance()
        Text(
            stringResource(
                R.string.expected_wash_price,
                formatter.format(washFoldPrice),
            )
        )

        AddressPicker(addresses, selectedAddress, onAddressSelected, navController)

        val pickupDateText = pickup.curSelectedDate?.let {
            convertMillisToDate(it) } ?: ""

        DateTimePicker(
            label =stringResource(R.string.pickup_select),
            text = pickupDateText,
            dateTimeValues = pickup,
            callbacks = pickupCallbacks
        )
        if(pickup.curSelectedTime != null) {
            val dropoffDateText = dropoff.curSelectedDate?.let {
                convertMillisToDate(it) } ?: ""
            DateTimePicker(
                label = stringResource(R.string.dropoff_select),
                text = dropoffDateText,
                dateTimeValues = dropoff,
                callbacks = dropoffCallbacks,
            )
        }
        LoadingButton(onBook, stringResource(R.string.book_now), bookEnabled, showBookingSpinner)
    }
}

