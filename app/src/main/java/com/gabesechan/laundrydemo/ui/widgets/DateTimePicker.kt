package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange

data class DateTimePickerValues(
    val selectableDates: SelectableDates,
    val curSelectedDate: Long?,
    val selectableTimes: List<TimeRange>,
    val curSelectedTime: TimeRange?,
) {
    fun toUtcTime(): Long {
        return curSelectedDate!!+curSelectedTime!!.startTime
    }
}

data class DateTimePickerCallbacks(
    val onDateSelected: (Long) -> Unit,
    val onTimeRangeSelected: (TimeRange)->Unit
)

@Composable
fun DateTimePicker(
    label: String? = null,
    placeholder: String? = null,
    text: String = "",
    dateTimeValues: DateTimePickerValues,
    callbacks: DateTimePickerCallbacks,
) {
    DatePickerTextfield(
        label = label,
        placeholder = placeholder,
        value = text,
        selectableDates =  dateTimeValues.selectableDates,
        onDateSelected = callbacks.onDateSelected
    )
    if(dateTimeValues.curSelectedDate != null) {
        DisplayTimes(
            dateTimeValues.selectableTimes,
            dateTimeValues.curSelectedTime,
            callbacks.onTimeRangeSelected
        )
    }

}


