package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.user.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

class LoginViewModelTest {

    @Test
    fun testInitialValues() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.LoginFailed
            }
        }
        val viewModel = LoginViewModel(loginAPI)

        assertTrue(viewModel.loginButtonEnabled.value)
        assertFalse(viewModel.showSpinner.value)
        assertEquals(0, viewModel.errorTextId.value)
    }


    @Test
    fun testLoginEnabledOffUntilFinishedFailure() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.LoginFailed
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")
        //Check immediate values
        assertFalse(viewModel.loginButtonEnabled.value)
        assertTrue(viewModel.showSpinner.value)

        //wait for update
        assertTrue(viewModel.loginButtonEnabled.drop(1).first())
        assertFalse(viewModel.showSpinner.value)

    }

    @Test
    fun testLoginEnabledOffUntilFinishedNetworkError() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.NetworkError
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")
        //Check immediate values
        assertFalse(viewModel.loginButtonEnabled.value)
        assertTrue(viewModel.showSpinner.value)

        //wait for update
        assertTrue(viewModel.loginButtonEnabled.drop(1).first())
        assertFalse(viewModel.showSpinner.value)

    }

    @Test
    fun testLoginEnabledOffUntilFinishedSuccess() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.LoginSuccess(User("1","Gabe","","", emptyList()))
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")
        //Check immediate values
        assertFalse(viewModel.loginButtonEnabled.value)
        assertTrue(viewModel.showSpinner.value)

        //wait for update
        assertTrue(viewModel.loginButtonEnabled.drop(1).first())
        assertFalse(viewModel.showSpinner.value)

    }

    @Test
    fun testErrorTextAfterAuthFailure() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.LoginFailed
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")

        assertEquals(R.string.bad_auth, viewModel.errorTextId.drop(1).first())

    }

    @Test
    fun testErrorTextAfterNetworkError() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.NetworkError
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")

        assertEquals(R.string.network_error, viewModel.errorTextId.drop(1).first())

    }

    @Test
    fun testErrorTextAfterSuccess() = runTest {
        val loginAPI = mockk<LoginAPI>{
            coEvery { login(any(), any()) } coAnswers {
                delay(100L)
                LoginAPI.LoginResult.LoginSuccess(User("1","Gabe","","", emptyList()))
            }
        }
        val viewModel = LoginViewModel(loginAPI)
        viewModel.onLoginClicked("","")

        //We delay instead of waiting for next because stateflows don't emit on repeated values
        delay(300L)
        assertEquals(0, viewModel.errorTextId.value)

    }

}