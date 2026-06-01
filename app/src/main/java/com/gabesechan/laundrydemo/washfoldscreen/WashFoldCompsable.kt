package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun WashFoldScreen(viewModel: WashFoldViewModel = hiltViewModel()) {
    val isLoaded by viewModel.dataLoaded.collectAsState()
    val pickupDate by viewModel.pickupDate.collectAsState(null)
    val pickupTime by viewModel.pickupTime.collectAsState(null)

    if(isLoaded) {
        WashFoldScreenInner(
            viewModel.getSelectablePickupDates(),
            viewModel.getPickupTimesForCurrentDate(),
            viewModel::setPickupDate,
            viewModel::setPickupTime,
            pickupDate,
            pickupTime,
        )
    }
}

@Composable
fun WashFoldScreenInner(
    pickupDates: SelectableDates,
    pickupTimes: List<TimeRange>,
    pickupDateSelected: (Long?)-> Unit,
    pickupTimeSelected: (TimeRange)->Unit,
    selectedPickupDate: Long?,
    selectedPickupTime: TimeRange?,
) {
    Column(Modifier.fillMaxHeight()) {
        Text(
            text = "Wash and Fold",
        )
        DatePickerDocked(
            stringResource(R.string.pickup_select),
            pickupDates,
            pickupDateSelected
        )
        if(selectedPickupDate != null) {
            Spacer(Modifier.height(12.dp))
            DisplayTimes(pickupTimes, selectedPickupTime, pickupTimeSelected)
        }
    }
}


@Composable
fun DisplayTimes(times: List<TimeRange>, selected: TimeRange?, onTimeSelected: (TimeRange)->Unit) {
    val dateFormat = SimpleDateFormat("hh:mma")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val unselectedModifier = Modifier.width(120.dp).height(32.dp).background(Color.Blue)
        val selectedModifier = Modifier.width(120.dp).height(32.dp).border(width = 2.dp, color = Color.Black)
            .background(Color.Blue)
        times.forEach{
            val startTime = dateFormat.format(it.startTime)
            val endTime = dateFormat.format(it.endTime)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = (if(selected == it) selectedModifier else unselectedModifier).clickable { onTimeSelected(it) }
            ) {
                Text(
                    text= stringResource(R.string.time_range_format, startTime, endTime),
                    color = Color.White,
                    fontSize = 8.sp,
                )
            }
        }

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

    //When the state updates, hide the dialog and notify the callback.  This is how we get away
    //without ok and cancel dialog buttons
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = formatter.format(Date(millis))

           showDatePicker = false
           onDateSelected(millis)
        }
    }

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

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                        },
                        dismissButton = {
                        },
                    ) {
                        DatePicker(state = datePickerState)
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

