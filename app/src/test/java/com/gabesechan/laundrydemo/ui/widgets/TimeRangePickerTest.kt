package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import io.mockk.mockk
import io.mockk.verify
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
class TimeRangePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val time1 = TimeRange(0L, 3600000L)
    private val time2 = TimeRange(3600000L, 7200000L)
    private val time3 = TimeRange(7200000L, 10800000L)

    private fun displayText(time: TimeRange): String {
        val dateFormat = SimpleDateFormat("hh:mma")
        return "${dateFormat.format(time.startTime)} - ${dateFormat.format(time.endTime)}"
    }

    @Test
    fun testDisplayTimesShowsAllPassedInTimes() {
        composeTestRule.setContent {
            DisplayTimes(
                times = listOf(time1, time2, time3),
                selected = null,
                onTimeSelected = {}
            )
        }

        composeTestRule.onNodeWithText(displayText(time1)).assertIsDisplayed()
        composeTestRule.onNodeWithText(displayText(time2)).assertIsDisplayed()
        composeTestRule.onNodeWithText(displayText(time3)).assertIsDisplayed()
    }

    @Test
    fun testNoneSelectedByDefault() {
        composeTestRule.setContent {
            DisplayTimes(
                times = listOf(time1, time2, time3),
                selected = null,
                onTimeSelected = {}
            )
        }

        composeTestRule.onNodeWithText(displayText(time1)).assertIsNotSelected()
        composeTestRule.onNodeWithText(displayText(time2)).assertIsNotSelected()
        composeTestRule.onNodeWithText(displayText(time3)).assertIsNotSelected()
    }

    @Test
    fun testSelectedTimeRangeDisplaysAsSelected() {
        composeTestRule.setContent {
            DisplayTimes(
                times = listOf(time1, time2, time3),
                selected = time2,
                onTimeSelected = {}
            )
        }

        composeTestRule.onNodeWithText(displayText(time1)).assertIsNotSelected()
        composeTestRule.onNodeWithText(displayText(time2)).assertIsSelected()
        composeTestRule.onNodeWithText(displayText(time3)).assertIsNotSelected()
    }

    @Test
    fun testClickingTimeRangeCallsOnTimeSelected() {
        val onTimeSelected = mockk<(TimeRange) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            DisplayTimes(
                times = listOf(time1, time2, time3),
                selected = null,
                onTimeSelected = onTimeSelected
            )
        }

        composeTestRule.onNodeWithText(displayText(time2)).performClick()

        verify(exactly = 1) { onTimeSelected(time2) }
    }
}
