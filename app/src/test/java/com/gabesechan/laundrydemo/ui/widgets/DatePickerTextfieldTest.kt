package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.SelectableDates
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Calendar
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class DatePickerTextfieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val noneSelectable = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean = false
        override fun isSelectableYear(year: Int): Boolean = false
    }

    @Test
    fun testPlaceholderDisplayedWhenValueIsEmpty() {
        composeTestRule.setContent {
            DatePickerTextfield(
                placeholder = "My Placeholder",
                value = "",
                onDateSelected = {}
            )
        }

        composeTestRule.onNodeWithText("My Placeholder").assertIsDisplayed()
    }

    @Test
    fun testValueDisplayedAndPlaceholderNotDisplayedWhenValueIsNonEmpty() {
        composeTestRule.setContent {
            DatePickerTextfield(
                placeholder = "My Placeholder",
                value = "My Value",
                onDateSelected = {}
            )
        }

        composeTestRule.onNodeWithText("My Value").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Placeholder").assertDoesNotExist()
    }

    @Test
    fun testLabelIsDisplayedWhenNonNull() {
        composeTestRule.setContent {
            DatePickerTextfield(
                label = "My Label",
                value = "",
                onDateSelected = {}
            )
        }

        composeTestRule.onNodeWithText("My Label").assertIsDisplayed()
    }

    @Test
    fun testClickingDateCallsOnDateSelectedWithMatchingValue() {
        val onDateSelected = mockk<(Long) -> Unit>(relaxed = true)
        val millisSlot = slot<Long>()

        composeTestRule.setContent {
            DatePickerTextfield(
                value = "",
                onDateSelected = onDateSelected
            )
        }

        composeTestRule.onNodeWithTag("PickerTextField").performTouchInput {
            down(center)
            up()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("15", substring = true, useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        verify(exactly = 1) { onDateSelected(capture(millisSlot)) }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = millisSlot.captured
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testClickingDateNotAllowedBySelectableDatesDoesNotCallOnDateSelected() {
        val onDateSelected = mockk<(Long) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            DatePickerTextfield(
                value = "",
                selectableDates = noneSelectable,
                onDateSelected = onDateSelected
            )
        }

        composeTestRule.onNodeWithTag("PickerTextField").performTouchInput {
            down(center)
            up()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("15", substring = true, useUnmergedTree = true).performTouchInput {
            down(center)
            up()
        }
        composeTestRule.waitForIdle()

        verify(exactly = 0) { onDateSelected(any()) }
    }
}
