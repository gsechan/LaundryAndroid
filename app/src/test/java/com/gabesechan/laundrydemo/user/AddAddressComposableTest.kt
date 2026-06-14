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
        street1: TextFieldState = TextFieldState(""),
        street2: TextFieldState = TextFieldState(""),
        city: TextFieldState = TextFieldState(""),
        state: TextFieldState = TextFieldState(""),
        country: TextFieldState = TextFieldState(""),
        postcode: TextFieldState = TextFieldState(""),
        onAddClicked: () -> Unit = {},
        createEnabled: Boolean = true,
        createSpinner: Boolean = false,
        navEvent: MutableSharedFlow<Unit> = MutableSharedFlow(1),
        navController: NavController = mockk(relaxed = true),
        networkError: Boolean = false,
    ) {
        composeTestRule.setContent {
            CreateAccountScreenInner(
                street1,
                street2,
                city,
                state,
                country,
                postcode,
                onAddClicked,
                createEnabled,
                createSpinner,
                navEvent,
                navController,
                networkError,
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
    fun testNetworkErrorShownWhenNetworkErrorIsTrue() {
        setContent(networkError = true)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertExists()
    }

    @Test
    fun testNetworkErrorNotShownWhenNetworkErrorIsFalse() {
        setContent(networkError = false)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertDoesNotExist()
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
    fun testValuesShownAndPlaceholdersHiddenWhenFieldsAreNonEmpty() {
        setContent(
            street1 = TextFieldState("123 Main St"),
            street2 = TextFieldState("Apt 4"),
            city = TextFieldState("Springfield"),
            state = TextFieldState("IL"),
            country = TextFieldState("USA"),
            postcode = TextFieldState("62701"),
        )

        composeTestRule.onNodeWithText("123 Main St").assertExists()
        composeTestRule.onNodeWithText("Apt 4").assertExists()
        composeTestRule.onNodeWithText("Springfield").assertExists()
        composeTestRule.onNodeWithText("IL").assertExists()
        composeTestRule.onNodeWithText("USA").assertExists()
        composeTestRule.onNodeWithText("62701").assertExists()

        composeTestRule.onNodeWithText(string(R.string.enter_street1)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_country)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_city)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_state)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_postcode)).assertDoesNotExist()
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
