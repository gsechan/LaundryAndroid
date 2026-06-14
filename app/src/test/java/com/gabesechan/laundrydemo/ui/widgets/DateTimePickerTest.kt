package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.text.SimpleDateFormat

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class DateTimePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val timeRange = TimeRange(0L, 3600000L)

    private fun displayText(time: TimeRange): String {
        val dateFormat = SimpleDateFormat("hh:mma")
        return "${dateFormat.format(time.startTime)} - ${dateFormat.format(time.endTime)}"
    }

    private fun valuesWithSelectedDate(selectedDate: Long?) = DateTimePickerValues(
        selectableDates = DatePickerDefaults.AllDates,
        curSelectedDate = selectedDate,
        selectableTimes = listOf(timeRange),
        curSelectedTime = null
    )

    private val callbacks = DateTimePickerCallbacks(
        onDateSelected = {},
        onTimeRangeSelected = {}
    )

    @Test
    fun testLabelIsDisplayedWhenNonNull() {
        composeTestRule.setContent {
            DateTimePicker(
                label = "My Label",
                dateTimeValues = valuesWithSelectedDate(null),
                callbacks = callbacks
            )
        }

        composeTestRule.onNodeWithText("My Label").assertIsDisplayed()
    }

    @Test
    fun testPlaceholderIsDisplayedWhenNonNullAndTextIsEmpty() {
        composeTestRule.setContent {
            DateTimePicker(
                placeholder = "My Placeholder",
                text = "",
                dateTimeValues = valuesWithSelectedDate(null),
                callbacks = callbacks
            )
        }

        composeTestRule.onNodeWithText("My Placeholder").assertIsDisplayed()
    }

    @Test
    fun testTextIsDisplayedAndPlaceholderIsNotWhenTextIsNonEmpty() {
        composeTestRule.setContent {
            DateTimePicker(
                placeholder = "My Placeholder",
                text = "My Text",
                dateTimeValues = valuesWithSelectedDate(null),
                callbacks = callbacks
            )
        }

        composeTestRule.onNodeWithText("My Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Placeholder").assertDoesNotExist()
    }

    @Test
    fun testNoTimeRangesDisplayedWhenCurSelectedDateIsNull() {
        composeTestRule.setContent {
            DateTimePicker(
                dateTimeValues = valuesWithSelectedDate(null),
                callbacks = callbacks
            )
        }

        composeTestRule.onNodeWithText(displayText(timeRange)).assertDoesNotExist()
    }

    @Test
    fun testTimeRangesDisplayedWhenCurSelectedDateIsNotNull() {
        composeTestRule.setContent {
            DateTimePicker(
                dateTimeValues = valuesWithSelectedDate(0L),
                callbacks = callbacks
            )
        }

        composeTestRule.onNodeWithText(displayText(timeRange)).assertIsDisplayed()
    }
}
