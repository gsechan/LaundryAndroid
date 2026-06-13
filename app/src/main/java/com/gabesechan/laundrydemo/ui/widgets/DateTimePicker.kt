package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import com.gabesechan.laundrydemo.laundromatinfo.AvailableDateTime
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import java.time.Instant
import java.time.ZoneId

class SelectableDeliveryDates(
    dates: List<AvailableDateTime>,
    earliestDay: Long
): SelectableDates {
    val allowedYears = mutableSetOf<Int>()
    val allowedDates = mutableSetOf<Long>()

    init {
        dates.forEach {
            if(it.date >= earliestDay) {
                allowedYears.add(
                    Instant.ofEpochMilli(it.date)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate().year
                )
                allowedDates.add(it.date)
            }
        }
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return allowedDates.contains(utcTimeMillis)
    }

    override fun isSelectableYear(year: Int): Boolean {
        return allowedYears.contains(year)
    }

}

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


