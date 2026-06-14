package com.gabesechan.laundrydemo.ui.widgets

import com.gabesechan.laundrydemo.laundromatinfo.AvailableDateTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class SelectableDeliveryDatesTest {

    private fun millisFor(year: Int, month: Int, day: Int): Long {
        return LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    private val date1 = millisFor(2024, 1, 15)
    private val date2 = millisFor(2025, 6, 20)

    @Test
    fun testIsSelectableYearReturnsTrueOnlyForYearsInList() {
        val selectableDates = SelectableDeliveryDates(
            dates = listOf(AvailableDateTime(date1, emptyList()), AvailableDateTime(date2, emptyList())),
            earliestDay = 0L
        )

        assertTrue(selectableDates.isSelectableYear(2024))
        assertTrue(selectableDates.isSelectableYear(2025))
        assertFalse(selectableDates.isSelectableYear(2023))
    }

    @Test
    fun testIsSelectableDateReturnsTrueOnlyForDatesInList() {
        val selectableDates = SelectableDeliveryDates(
            dates = listOf(AvailableDateTime(date1, emptyList()), AvailableDateTime(date2, emptyList())),
            earliestDay = 0L
        )

        assertTrue(selectableDates.isSelectableDate(date1))
        assertTrue(selectableDates.isSelectableDate(date2))
        assertFalse(selectableDates.isSelectableDate(millisFor(2024, 1, 16)))
    }

    @Test
    fun testDatesBeforeEarliestDayAreIgnored() {
        val selectableDates = SelectableDeliveryDates(
            dates = listOf(AvailableDateTime(date1, emptyList()), AvailableDateTime(date2, emptyList())),
            earliestDay = date2
        )

        assertFalse(selectableDates.isSelectableYear(2024))
        assertFalse(selectableDates.isSelectableDate(date1))

        assertTrue(selectableDates.isSelectableYear(2025))
        assertTrue(selectableDates.isSelectableDate(date2))
    }
}
