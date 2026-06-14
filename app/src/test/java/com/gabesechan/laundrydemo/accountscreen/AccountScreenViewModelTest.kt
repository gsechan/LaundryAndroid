package com.gabesechan.laundrydemo.accountscreen

import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AccountScreenViewModelTest {

    @Test
    fun testUserExposesUserRepositoryCurrent() {
        val loggedInUser = User("gabe", "gabe@example.com", "1234567890", emptyList())
        val userRepository = mockk<UserRepository> {
            every { current } returns MutableStateFlow(loggedInUser).asStateFlow()
        }
        val loginAPI = mockk<LoginAPI>()

        val viewModel = AccountScreenViewModel(loginAPI, userRepository)

        assertEquals(loggedInUser, viewModel.user.value)
    }

    @Test
    fun testOnLogoutClickedCallsLoginAPILogout() = runTest {
        val userRepository = mockk<UserRepository> {
            every { current } returns MutableStateFlow(User.NoUser).asStateFlow()
        }
        val loginAPI = mockk<LoginAPI> {
            coEvery { logout() } returns Unit
        }

        val viewModel = AccountScreenViewModel(loginAPI, userRepository)
        viewModel.onLogoutClicked()

        coVerify(exactly = 1) { loginAPI.logout() }
    }
}
