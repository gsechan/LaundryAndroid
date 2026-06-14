package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.SelectableDates
import com.gabesechan.laundrydemo.laundromatinfo.AvailableDateTime
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
