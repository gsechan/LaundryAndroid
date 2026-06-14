package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.ui.test.assertIsDisplayed
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
class LoadingButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val buttonText = "Book Now"

    @Test
    fun testSpinnerHiddenShowsTextAndNoSpinner() {
        composeTestRule.setContent {
            LoadingButton(onClick = {}, text = buttonText, enabled = true, showSpinner = false)
        }

        composeTestRule.onNodeWithText(buttonText).assertIsDisplayed()
        composeTestRule.onNodeWithTag("LoadingProgress").assertDoesNotExist()
    }

    @Test
    fun testSpinnerShownHidesTextAndShowsSpinner() {
        composeTestRule.setContent {
            LoadingButton(onClick = {}, text = buttonText, enabled = true, showSpinner = true)
        }

        composeTestRule.onNodeWithTag("LoadingProgress").assertIsDisplayed()
        composeTestRule.onNodeWithText(buttonText).assertDoesNotExist()
    }

    @Test
    fun testClickWhenEnabledCallsOnClick() {
        val onClick = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            LoadingButton(onClick = onClick, text = buttonText, enabled = true, showSpinner = false)
        }

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 1) { onClick() }
    }

    @Test
    fun testClickWhenDisabledDoesNothing() {
        val onClick = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            LoadingButton(onClick = onClick, text = buttonText, enabled = false, showSpinner = false)
        }

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 0) { onClick() }
    }
}
