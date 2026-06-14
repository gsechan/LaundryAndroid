package com.gabesechan.laundrydemo.drycleaningscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.models.Item
import com.gabesechan.laundrydemo.ui.widgets.AddressPicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePicker
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerCallbacks
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton
import com.gabesechan.laundrydemo.ui.widgets.NumberPicker
import com.gabesechan.laundrydemo.ui.widgets.convertMillisToDate
import com.gabesechan.laundrydemo.models.Address
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun DryCleaningComposable(navController: NavController, viewModel: DryCleaningViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val isBooked by viewModel.isBooked.collectAsState()
    val pickupDateValues by viewModel.pickupDateValues.collectAsState()
    val dropoffDateValues by viewModel.dropoffDateValues.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val addresses by viewModel.addresses.collectAsState(emptyList())
    val itemCounts by viewModel.itemCounts.collectAsState()
    val bookEnabled by viewModel.bookEnabled.collectAsState()
    val showBookingSpinner by viewModel.showBookingSpinner.collectAsState()

    DryCleaningComposableInner(
        isBooked,
        viewModel.dataError,
        isLoaded,
        addresses,
        selectedAddress,
        viewModel::selectAddress,
        pickupDateValues,
        DateTimePickerCallbacks(
            viewModel::setPickupDate, viewModel::setPickupTime
        ),
        dropoffDateValues,
        DateTimePickerCallbacks(
            viewModel::setDropoffDate, viewModel::setDropoffTime
        ),
        viewModel::book,
        itemCounts,
        viewModel::onCountChanged,
        viewModel.items,
        bookEnabled,
        showBookingSpinner,
        navController
    )
}

@Composable
fun DryCleaningComposableInner(
    isBooked: Boolean,
    dataError: Boolean,
    isLoaded: Boolean,
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressSelected: (Address)->Unit,
    pickup: DateTimePickerValues,
    pickupCallbacks: DateTimePickerCallbacks,
    dropoff: DateTimePickerValues,
    dropoffCallbacks: DateTimePickerCallbacks,
    onBook: ()->Unit,
    itemCounts: Map<String, Int>,
    onCountChanged: (String, Int)->Unit,
    items:List<Item>,
    buttonEnabled: Boolean,
    showBookingSpinner: Boolean,
    navController: NavController
) {
    if(isBooked) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.order_booked))
        }
        return
    }
    else if(dataError) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.network_error))
        }
        return
    }
    else if(!isLoaded) {
        return
    }

    val formatter = NumberFormat.getCurrencyInstance()

    Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        var totalCost = BigDecimal(0)
        items.forEach { item->
            val cost = item.price * BigDecimal(itemCounts[item.id]!!)
            totalCost = totalCost.plus(cost)
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, modifier = Modifier.alignByBaseline().fillMaxWidth(.2f))
                NumberPicker(
                    itemCounts[item.id]!!,
                    {onCountChanged(item.id, it)},
                    0,
                    10
                    , modifier = Modifier.alignByBaseline()
                )
                Text(formatter.format(cost),modifier = Modifier.alignByBaseline())
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total price:")
            Text(formatter.format(totalCost))
        }

        AddressPicker(addresses, selectedAddress, onAddressSelected, navController)
        val pickupDateText = pickup.curSelectedDate?.let {
            convertMillisToDate(it) } ?: ""


        DateTimePicker(
            label = stringResource(R.string.pickup_select),
            text = pickupDateText,
            dateTimeValues = pickup,
            callbacks = pickupCallbacks
        )
        if(pickup.curSelectedTime != null) {
            val dropoffDateText = dropoff.curSelectedDate?.let {
                convertMillisToDate(it) } ?: ""

            DateTimePicker(
                label =stringResource(R.string.dropoff_select),
                text = dropoffDateText,
                dateTimeValues = dropoff,
                callbacks = dropoffCallbacks,
            )
        }
        LoadingButton(onBook, stringResource(R.string.book_now), buttonEnabled, showBookingSpinner)
    }
}

