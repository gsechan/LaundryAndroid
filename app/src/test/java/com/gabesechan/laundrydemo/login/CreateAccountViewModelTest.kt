package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshots.Snapshot
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.models.User
import com.google.i18n.phonenumbers.PhoneNumberUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.apache.commons.validator.routines.EmailValidator
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAccountViewModelTest {

    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var validator: EmailValidator

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        phoneNumberUtil = PhoneNumberUtil.getInstance()
        validator = EmailValidator.getInstance()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setText(state: TextFieldState, text: String) {
        state.setTextAndPlaceCursorAtEnd(text)
        Snapshot.sendApplyNotifications()
    }

    @Test
    fun testCreateEnabledFalseInitially() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledTrueWhenAllFieldsValid() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        setText(viewModel.name, "Gabe")
        setText(viewModel.phone, "12345")
        setText(viewModel.email, "gabe@example.com")
        setText(viewModel.password1, "password1")
        setText(viewModel.password2, "password1")
        advanceUntilIdle()

        assertTrue(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledFalseWhenPasswordsDontMatch() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        setText(viewModel.name, "Gabe")
        setText(viewModel.phone, "12345")
        setText(viewModel.email, "gabe@example.com")
        setText(viewModel.password1, "password1")
        setText(viewModel.password2, "password2")
        advanceUntilIdle()

        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledFalseWhilePosting() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val loginAPI = mockk<LoginAPI> {
            coEvery { createAccount(any(), any(), any(), any()) } coAnswers {
                kotlinx.coroutines.CompletableDeferred<LoginAPI.LoginResult>().await()
            }
        }
        val viewModel = CreateAccountViewModel(loginAPI, phoneNumberUtil, validator)

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        setText(viewModel.name, "Gabe")
        setText(viewModel.phone, "12345")
        setText(viewModel.email, "gabe@example.com")
        setText(viewModel.password1, "password1")
        setText(viewModel.password2, "password1")
        advanceUntilIdle()

        assertTrue(viewModel.createEnabled.value)

        viewModel.createAccountClicked()
        advanceUntilIdle()

        assertTrue(viewModel.createRunning.value)
        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testPasswordSupportingTextShowsErrorForShortPassword() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.passWordSuppotingText.collect {} }
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.passWordSuppotingText.value)

        setText(viewModel.password1, "short")
        advanceUntilIdle()

        assertEquals(R.string.invalid_password_length, viewModel.passWordSuppotingText.value)

        setText(viewModel.password1, "longenough1")
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.passWordSuppotingText.value)

        job.cancel()
    }

    @Test
    fun testPasswordSupportingText2ShowsErrorWhenPasswordsDontMatch() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.passWordSuppotingText2.collect {} }
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.passWordSuppotingText2.value)

        setText(viewModel.password1, "password1")
        setText(viewModel.password2, "password2")
        advanceUntilIdle()

        assertEquals(R.string.passwords_must_match, viewModel.passWordSuppotingText2.value)

        setText(viewModel.password2, "password1")
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.passWordSuppotingText2.value)

        job.cancel()
    }

    @Test
    fun testPhoneSupportingTextValidatesNumber() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.phoneSupportingText.collect {} }
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.phoneSupportingText.value)

        setText(viewModel.phone, "123")
        advanceUntilIdle()

        assertEquals(R.string.invalid_phone_number, viewModel.phoneSupportingText.value)

        setText(viewModel.phone, "2065551234")
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.phoneSupportingText.value)

        job.cancel()
    }

    @Test
    fun testEmailSupportingTextValidatesEmail() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = CreateAccountViewModel(mockk(), phoneNumberUtil, validator)

        val job = launch { viewModel.emailSupportingText.collect {} }
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.emailSupportingText.value)

        setText(viewModel.email, "notanemail")
        advanceUntilIdle()

        assertEquals(R.string.invalid_email, viewModel.emailSupportingText.value)

        setText(viewModel.email, "gabe@example.com")
        advanceUntilIdle()

        assertEquals(R.string.empty, viewModel.emailSupportingText.value)

        job.cancel()
    }

    @Test
    fun testCreateAccountClickedCallsLoginAPIAndResetsRunning() = runTest {
        val user = User("gabe", "gabe@example.com", "1234567890", emptyList())
        val loginAPI = mockk<LoginAPI> {
            coEvery { createAccount(any(), any(), any(), any()) } returns LoginAPI.LoginResult.LoginSuccess(user)
        }
        val viewModel = CreateAccountViewModel(loginAPI, phoneNumberUtil, validator)

        setText(viewModel.name, "gabe")
        setText(viewModel.password1, "password1")
        setText(viewModel.phone, "1234567890")
        setText(viewModel.email, "gabe@example.com")

        viewModel.createAccountClicked()
        assertTrue(viewModel.createRunning.value)

        viewModel.createRunning.first { !it }

        coVerify(exactly = 1) {
            loginAPI.createAccount("gabe", "password1", "1234567890", "gabe@example.com")
        }
        assertFalse(viewModel.createRunning.value)
    }

    @Test
    fun testCreateAccountClickedSetsNetworkErrorOnNetworkError() = runTest {
        val loginAPI = mockk<LoginAPI> {
            coEvery { createAccount(any(), any(), any(), any()) } returns LoginAPI.LoginResult.NetworkError
        }
        val viewModel = CreateAccountViewModel(loginAPI, phoneNumberUtil, validator)

        setText(viewModel.name, "gabe")
        setText(viewModel.password1, "password1")
        setText(viewModel.phone, "1234567890")
        setText(viewModel.email, "gabe@example.com")

        viewModel.createAccountClicked()
        viewModel.createRunning.first { !it }

        assertTrue(viewModel.neworkError)
    }
}
