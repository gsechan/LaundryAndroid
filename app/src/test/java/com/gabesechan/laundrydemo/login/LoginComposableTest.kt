package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w480dp-h800dp")
class LoginComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(resId: Int): String {
        return ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId)
    }

    private fun setContent(
        phone: TextFieldState = TextFieldState(""),
        password: TextFieldState = TextFieldState(""),
        loginFunc: () -> Unit = {},
        loginEnabled: Boolean = true,
        showSpinner: Boolean = false,
        errorTextId: Int = 0,
        navController: NavController = mockk(relaxed = true),
    ) {
        composeTestRule.setContent {
            LoginInner(
                phone,
                password,
                loginFunc,
                loginEnabled,
                showSpinner,
                errorTextId,
                navController,
            )
        }
    }

    private fun fillInCredentials(username: String, password: String) {
        composeTestRule.onNodeWithText(string(R.string.username)).performTextInput(username)
        composeTestRule.onNodeWithText(string(R.string.password)).performTextInput(password)
    }

    @Test
    fun testLogoIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithTag("Logo").assertExists()
    }

    @Test
    fun testErrorTextDisplayedWhenErrorTextIdIsNonZero() {
        setContent(errorTextId = R.string.network_error)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertExists()
    }

    @Test
    fun testErrorTextNotDisplayedWhenErrorTextIdIsZero() {
        setContent(errorTextId = 0)

        composeTestRule.onNodeWithText(string(R.string.network_error)).assertDoesNotExist()
    }

    @Test
    fun testLoginAndCreateAccountButtonsAreDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.login_button)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.create_account)).assertExists()
    }

    @Test
    fun testLoginButtonEnabledOnlyIfLoginEnabledIsTrue() {
        setContent(loginEnabled = true)

        fillInCredentials("myuser", "secret123")

        composeTestRule.onNodeWithText(string(R.string.login_button)).assertIsEnabled()
    }

    @Test
    fun testLoginButtonDisabledWhenLoginEnabledIsFalse() {
        setContent(loginEnabled = false)

        fillInCredentials("myuser", "secret123")

        composeTestRule.onNodeWithText(string(R.string.login_button)).assertIsNotEnabled()
    }

    @Test
    fun testSpinnerShownOnlyWhenShowSpinnerIsTrue() {
        setContent(showSpinner = true)

        composeTestRule.onNodeWithTag("LoadingProgress").assertExists()
    }

    @Test
    fun testSpinnerNotShownWhenShowSpinnerIsFalse() {
        setContent(showSpinner = false)

        composeTestRule.onNodeWithTag("LoadingProgress").assertDoesNotExist()
    }

    @Test
    fun testClickingLoginCallsLoginFunc() {
        var loginCalled = false
        val loginFunc: () -> Unit = { loginCalled = true }

        setContent(loginFunc = loginFunc, loginEnabled = true)

        fillInCredentials("myuser", "secret123")

        composeTestRule.onNodeWithText(string(R.string.login_button)).performClick()

        assertEquals(true, loginCalled)
    }

    @Test
    fun testClickingCreateAccountNavigates() {
        val navController = mockk<NavController>(relaxed = true)

        setContent(navController = navController)

        composeTestRule.onNodeWithText(string(R.string.create_account)).performClick()

        verify(exactly = 1) { navController.navigate("createAccount") }
    }
}
