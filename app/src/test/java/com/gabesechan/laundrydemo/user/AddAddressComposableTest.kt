package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class AddAddressComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(resId: Int): String {
        return ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId)
    }

    private fun setContent(
        onAddClicked: () -> Unit = {},
        createEnabled: Boolean = true,
        createSpinner: Boolean = false,
        navEvent: MutableSharedFlow<Unit> = MutableSharedFlow(1),
        navController: NavController = mockk(relaxed = true),
    ) {
        composeTestRule.setContent {
            CreateAccountScreenInner(
                TextFieldState(""),
                TextFieldState(""),
                TextFieldState(""),
                TextFieldState(""),
                TextFieldState(""),
                TextFieldState(""),
                onAddClicked,
                createEnabled,
                createSpinner,
                navEvent,
                navController,
            )
        }
    }

    @Test
    fun testNavEventEmitCallsPopBackStack() {
        val navController = mockk<NavController>(relaxed = true)
        val navEvent = MutableSharedFlow<Unit>(1)

        setContent(navEvent = navEvent, navController = navController)

        navEvent.tryEmit(Unit)
        composeTestRule.waitForIdle()

        verify(exactly = 1) { navController.popBackStack() }
    }

    @Test
    fun testAllSixTextFieldsAppear() {
        setContent()

        composeTestRule.onAllNodesWithText(string(R.string.enter_street1)).assertCountEquals(2)
        composeTestRule.onNodeWithText(string(R.string.enter_country)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.enter_city)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.enter_state)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.enter_postcode)).assertExists()
    }

    @Test
    fun testAddNewAddressButtonAppears() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.add_new_address)).assertExists()
    }

    @Test
    fun testButtonEnabledOnlyIfCreateEnabledIsTrue() {
        setContent(createEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsEnabled()
    }

    @Test
    fun testButtonDisabledWhenCreateEnabledIsFalse() {
        setContent(createEnabled = false)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsNotEnabled()
    }

    @Test
    fun testSpinnerShownOnlyWhenCreateSpinnerIsTrue() {
        setContent(createSpinner = true)

        composeTestRule.onNodeWithTag("LoadingProgress").assertExists()
    }

    @Test
    fun testSpinnerNotShownWhenCreateSpinnerIsFalse() {
        setContent(createSpinner = false)

        composeTestRule.onNodeWithTag("LoadingProgress").assertDoesNotExist()
    }

    @Test
    fun testClickingEnabledButtonCallsOnAddClicked() {
        val onAddClicked = mockk<() -> Unit>(relaxed = true)
        setContent(onAddClicked = onAddClicked, createEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 1) { onAddClicked() }
    }
}
