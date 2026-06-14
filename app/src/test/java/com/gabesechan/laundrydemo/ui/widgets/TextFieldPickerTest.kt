package com.gabesechan.laundrydemo.ui.widgets

import android.view.KeyEvent
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.gabesechan.laundrydemo.R
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class TextFieldPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLabelIsDisplayedWhenNotNull() {
        composeTestRule.setContent {
            TextFieldPicker<String>(
                label = "My Label",
                value = "",
                dialogContent = {},
                onSelected = {},
                showDialogState = remember { mutableStateOf(false) }
            )
        }

        composeTestRule.onNodeWithText("My Label").assertIsDisplayed()
    }

    @Test
    fun testValueIsDisplayedWhenNonEmpty() {
        composeTestRule.setContent {
            TextFieldPicker<String>(
                value = "Some Value",
                dialogContent = {},
                onSelected = {},
                showDialogState = remember { mutableStateOf(false) }
            )
        }

        composeTestRule.onNodeWithText("Some Value").assertIsDisplayed()
    }

    @Test
    fun testPlaceholderIsDisplayedWhenValueIsEmpty() {
        composeTestRule.setContent {
            TextFieldPicker<String>(
                value = "",
                placeholder = "My Placeholder",
                dialogContent = {},
                onSelected = {},
                showDialogState = remember { mutableStateOf(false) }
            )
        }

        composeTestRule.onNodeWithText("My Placeholder").assertIsDisplayed()
    }

    @Test
    fun testIconIsDisplayedWhenNotNull() {
        composeTestRule.setContent {
            TextFieldPicker<String>(
                value = "",
                icon = R.drawable.date_range,
                dialogContent = {},
                onSelected = {},
                showDialogState = remember { mutableStateOf(false) }
            )
        }

        composeTestRule.onNodeWithContentDescription("Select", useUnmergedTree = true).assertIsDisplayed()
    }
    
    @Test
    fun testDialogContentIsDisplayedWhenShowDialogIsTrue() {
        composeTestRule.setContent {
            TextFieldPicker<String>(
                value = "",
                dialogContent = { Text("Dialog Content") },
                onSelected = {},
                showDialogState = remember { mutableStateOf(true) }
            )
        }

        composeTestRule.onNodeWithText("Dialog Content").assertIsDisplayed()
    }

    @Test
    fun testDismissingDialogSetsShowDialogToFalse() {
        val showDialogState = mutableStateOf(true)

        composeTestRule.setContent {
            TextFieldPicker<String>(
                value = "",
                dialogContent = {},
                onSelected = {},
                showDialogState = showDialogState
            )
        }

        val dialog = ShadowDialog.getLatestDialog()
        dialog.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dialog.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
        composeTestRule.waitForIdle()

        assertFalse(showDialogState.value)
    }

    @Test
    fun testDialogContentCallbackCallsOnSelectedWithSameValue() {
        val onSelected = mockk<(String) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            TextFieldPicker(
                value = "",
                dialogContent = { select -> LaunchedEffect(Unit) { select("chosen") } },
                onSelected = onSelected,
                showDialogState = remember { mutableStateOf(true) }
            )
        }

        composeTestRule.waitForIdle()

        verify(exactly = 1) { onSelected("chosen") }
    }
}
