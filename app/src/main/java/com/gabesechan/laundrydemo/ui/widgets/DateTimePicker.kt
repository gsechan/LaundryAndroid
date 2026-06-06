package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange


data class DateTimePickerValues(
    val selectableDates: SelectableDates,
    val curSelectedDate: Long?,
    val selectableTimes: List<TimeRange>,
    val curSelectedTime: TimeRange?,
)

data class DateTimePickerCallbacks(
    val onDateSelected: (Long) -> Unit,
    val onTimeRangeSelected: (TimeRange)->Unit
)

@Composable
fun DateTimePicker(
    label: String? = null,
    placeholder: String? = null,
    dateTimeValues: DateTimePickerValues,
    callbacks: DateTimePickerCallbacks,
) {
    DatePickerTextfield(
        label = label,
        placeholder = placeholder,
        selectableDates =  dateTimeValues.selectableDates,
        onDateSelected = callbacks.onDateSelected
    )
    if(dateTimeValues.curSelectedDate != null) {
        Spacer(Modifier.height(12.dp))
        DisplayTimes(
            dateTimeValues.selectableTimes,
            dateTimeValues.curSelectedTime,
            callbacks.onTimeRangeSelected
        )
    }

}


