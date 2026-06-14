package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class NumberPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMinusAndPlusAndValueAreDisplayed() {
        composeTestRule.setContent {
            NumberPicker(value = 5, onValueChange = {}, min = 0, max = 10)
        }

        composeTestRule.onNodeWithText("-").assertIsDisplayed()
        composeTestRule.onNodeWithText("+").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun testDecrementButtonDisabledAtMin() {
        composeTestRule.setContent {
            NumberPicker(value = 0, onValueChange = {}, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("DecrementButton").assertIsNotEnabled()
    }

    @Test
    fun testDecrementButtonDisabledBelowMin() {
        composeTestRule.setContent {
            NumberPicker(value = -1, onValueChange = {}, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("DecrementButton").assertIsNotEnabled()
    }

    @Test
    fun testIncrementButtonDisabledAtMax() {
        composeTestRule.setContent {
            NumberPicker(value = 10, onValueChange = {}, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("IncrementButton").assertIsNotEnabled()
    }

    @Test
    fun testIncrementButtonDisabledAboveMax() {
        composeTestRule.setContent {
            NumberPicker(value = 11, onValueChange = {}, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("IncrementButton").assertIsNotEnabled()
    }

    @Test
    fun testClickingIncrementCallsOnValueChangeWithIncreasedValue() {
        val onValueChange = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NumberPicker(value = 5, onValueChange = onValueChange, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("IncrementButton").performClick()

        verify(exactly = 1) { onValueChange(6) }
    }

    @Test
    fun testClickingDecrementCallsOnValueChangeWithDecreasedValue() {
        val onValueChange = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NumberPicker(value = 5, onValueChange = onValueChange, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("DecrementButton").performClick()

        verify(exactly = 1) { onValueChange(4) }
    }

    @Test
    fun testClickingDisabledIncrementDoesNotCallOnValueChange() {
        val onValueChange = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NumberPicker(value = 10, onValueChange = onValueChange, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("IncrementButton").performClick()

        verify(exactly = 0) { onValueChange(any()) }
    }

    @Test
    fun testClickingDisabledDecrementDoesNotCallOnValueChange() {
        val onValueChange = mockk<(Int) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            NumberPicker(value = 0, onValueChange = onValueChange, min = 0, max = 10)
        }

        composeTestRule.onNodeWithTag("DecrementButton").performClick()

        verify(exactly = 0) { onValueChange(any()) }
    }
}
