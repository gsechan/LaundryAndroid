package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/*
@RunWith(AndroidJUnit4::class)
class LoadingButtonTest {

Commented out until I can get a PC this can work on.
It apparently has issues with spaces in filenames, and I
don't want to add a new administrator to my PC to fix that
    @Test fun testEnabledAndNoSpinner() {
        rule.setContent {
            LoadingButton({}, "Text", true, false)
        }
        rule.onNodeWithText("Text").assertExists()
        rule.onNodeWithTag("LoadingProgress").assertDoesNotExist()
        rule.onNodeWithTag("LoadingButtonRoot").assertIsEnabled()
    }

    @Test fun testDisabledAndSpinner() {
        rule.setContent {
            LoadingButton({}, "Text", true, false)
        }
        rule.onNodeWithText("Text").assertDoesNotExist()
        rule.onNodeWithTag("LoadingProgress").assertExists()
        rule.onNodeWithTag("LoadingButtonRoot").assertIsNotEnabled()
    }

    @Test fun testClickingButtonCallsFunction() {
        var isClicked = false
        rule.setContent {
            LoadingButton({ isClicked = true}, "Text", true, false)
        }
        rule.onNodeWithTag("LoadingButtonRoot").performClick()
        assertTrue(isClicked)

    }


}
 */
