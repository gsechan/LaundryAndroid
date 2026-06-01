package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WashFoldScreen(viewModel: WashFoldViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    if(isLoaded) {
        WashFoldScreenInner(viewModel.getSelectablePickupDates())
    }
}

@Composable
fun WashFoldScreenInner(pickupDates: SelectableDates) {
    Column(Modifier.fillMaxHeight()) {
        Text(
            text = "Wash and Fold",
        )
        DatePickerDocked(stringResource(R.string.pickup_select), pickupDates, {})
    }
}


@Composable
fun DatePickerDocked(placeholder: String, selectableDates: SelectableDates?, onDateSelected: (Long?) -> Unit) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = if(selectableDates != null) selectableDates else DatePickerDefaults.AllDates
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    //When the state updates, hide the dialog and notify the callback
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = formatter.format(Date(millis))

           showDatePicker = false
           onDateSelected(millis)
        }
    }
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp
    val screenWidthDp = configuration.screenWidthDp



    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text(placeholder) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        painterResource( R.drawable.date_range),
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .pointerInput(selectedDate) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDatePicker = true
                        }
                    }
                }

        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    /*
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )*/
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                        },
                        dismissButton = {
                        },
                        modifier = Modifier
                            .widthIn(max = screenWidthDp.dp)
                            .requiredHeightIn(max = (screenHeightDp * 0.75).dp) // Scales to 75% of screen height
                    ) {
                        DatePicker(state = datePickerState
                        )
                    }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

