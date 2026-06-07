package com.gabesechan.laundrydemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.gabesechan.laundrydemo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * Displays a text field.  Clicking on it allows you to select a date via a modal popup.  Selecting
 * a date closes the modal
 *
 * placeholder-  the label text for the field
 * selectableDates-  a filter allowing or denying dates from being selected
 * onDateSelected-  a callback used to inform when a date was selected.
 */
@Composable
fun DatePickerTextfield(
    placeholder: String? = null,
    value: String = "",
    label: String? = null,
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    onDateSelected: (Long) -> Unit
) {
    var showDialog =  rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDates
    )

    //When the state updates, hide the dialog and notify the callback.  This is how we get away
    //without ok and cancel dialog buttons
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            showDialog.value = false
            onDateSelected(millis)
        }
    }

    TextFieldPicker(
        placeholder = placeholder,
        label = label,
        value = value,
        dialogContent = {
            DatePicker(state = datePickerState)
        },
        onSelected = onDateSelected,
        icon = R.drawable.date_range,
        showDialogState = showDialog
    )

}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}
