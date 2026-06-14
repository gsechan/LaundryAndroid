package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
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
class CreateAccountScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(resId: Int): String {
        return ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId)
    }

    private fun setContent(
        nameState: TextFieldState = TextFieldState(""),
        password1: TextFieldState = TextFieldState(""),
        password2: TextFieldState = TextFieldState(""),
        phoneState: TextFieldState = TextFieldState(""),
        emailState: TextFieldState = TextFieldState(""),
        password1SupportText: Int = R.string.network_error,
        password2SupportText: Int = R.string.order_booked,
        phoneSupportText: Int = R.string.logout,
        emailSupportText: Int = R.string.addresses,
        onCreateClicked: () -> Unit = {},
        createEnabled: Boolean = true,
        createSpinner: Boolean = false,
        networkError: Boolean = false,
    ) {
        composeTestRule.setContent {
            CreateAccountScreenInner(
                networkError,
                nameState,
                password1,
                password1SupportText,
                password2,
                password2SupportText,
                phoneState,
                phoneSupportText,
                emailState,
                emailSupportText,
                onCreateClicked,
                createEnabled,
                createSpinner,
            )
        }
    }

    @Test
    fun testNetworkErrorShownWhenNetworkErrorIsTrue() {
        setContent(networkError = true)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.enter_name)).assertDoesNotExist()
    }

    @Test
    fun testNetworkErrorNotShownWhenNetworkErrorIsFalse() {
        setContent(networkError = false, password1SupportText = R.string.order_booked)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertDoesNotExist()
    }

    @Test
    fun testPlaceholdersDisplayedWhenFieldsAreEmpty() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.enter_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.password)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.repeat_password)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.enter_phone)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.enter_email)).assertIsDisplayed()
    }

    @Test
    fun testValuesDisplayedWhenFieldsAreNonEmpty() {
        setContent(
            nameState = TextFieldState("John Doe"),
            password1 = TextFieldState("secret1"),
            password2 = TextFieldState("secret22"),
            phoneState = TextFieldState("5551234567"),
            emailState = TextFieldState("john@example.com"),
        )

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("•".repeat("secret1".length)).assertIsDisplayed()
        composeTestRule.onNodeWithText("•".repeat("secret22".length)).assertIsDisplayed()
        composeTestRule.onNodeWithText("5551234567").assertIsDisplayed()
        composeTestRule.onNodeWithText("john@example.com").assertIsDisplayed()

        composeTestRule.onNodeWithText(string(R.string.enter_name)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.password)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.repeat_password)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_phone)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.enter_email)).assertDoesNotExist()
    }

    @Test
    fun testSupportingTextsAreDisplayed() {
        setContent(
            password1SupportText = R.string.network_error,
            password2SupportText = R.string.order_booked,
            phoneSupportText = R.string.logout,
            emailSupportText = R.string.addresses,
        )

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.order_booked)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.logout)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.addresses)).assertIsDisplayed()
    }

    @Test
    fun testCreateAccountButtonIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.create_account)).assertExists()
    }

    @Test
    fun testCreateAccountButtonEnabledWhenCreateEnabledIsTrue() {
        setContent(createEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsEnabled()
    }

    @Test
    fun testCreateAccountButtonDisabledWhenCreateEnabledIsFalse() {
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
    fun testClickingEnabledButtonCallsOnCreateClicked() {
        val onCreateClicked = mockk<() -> Unit>(relaxed = true)
        setContent(onCreateClicked = onCreateClicked, createEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 1) { onCreateClicked() }
    }
}
