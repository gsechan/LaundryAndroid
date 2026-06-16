package com.gabesechan.laundrydemo.accountscreen

import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.network.NetworkResponse
import com.gabesechan.laundrydemo.login.UserRepository
import com.gabesechan.laundrydemo.user.UserServer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Test

class AccountScreenViewModelTest {

    private val address1 = Address("1", "123 Main St", null, "Springfield", "IL", "USA", "62701")
    private val address2 = Address("2", "456 Oak Ave", "Apt 2", "Springfield", "IL", "USA", "62702")

    @Test
    fun testUserExposesUserRepositoryCurrent() {
        val loggedInUser = User("gabe", "gabe@example.com", "1234567890", emptyList())
        val userRepository = mockk<UserRepository> {
            every { current } returns MutableStateFlow(loggedInUser).asStateFlow()
        }
        val loginAPI = mockk<LoginAPI>()
        val userServer = mockk<UserServer>()

        val viewModel = AccountScreenViewModel(loginAPI, userRepository, userServer)

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
        val userServer = mockk<UserServer>()

        val viewModel = AccountScreenViewModel(loginAPI, userRepository, userServer)
        viewModel.onLogoutClicked()

        coVerify(exactly = 1) { loginAPI.logout() }
    }

    @Test
    fun testDeleteAddressSuccessRemovesAddressFromUser() = runTest {
        val loggedInUser = User("gabe", "gabe@example.com", "1234567890", listOf(address1, address2))
        val updatedUser = loggedInUser.copy(addresses = listOf(address2))
        val userRepository = mockk<UserRepository> {
            every { current } returns MutableStateFlow(loggedInUser).asStateFlow()
            every { setUser(any()) } just Runs
        }
        val loginAPI = mockk<LoginAPI>()
        val userServer = mockk<UserServer> {
            coEvery { deleteAddress("1") } returns NetworkResponse(true, null, emptyList(), Unit)
        }

        val viewModel = AccountScreenViewModel(loginAPI, userRepository, userServer)
        viewModel.deleteAddress(address1)
        viewModel.deleteAddressRunning.first { !it }

        coVerify(exactly = 1) { userServer.deleteAddress("1") }
        verify(exactly = 1) { userRepository.setUser(updatedUser) }
        assertFalse(viewModel.networkError)
    }

    @Test
    fun testDeleteAddressNetworkErrorSetsNetworkError() = runTest {
        val loggedInUser = User("gabe", "gabe@example.com", "1234567890", listOf(address1))
        val userRepository = mockk<UserRepository> {
            every { current } returns MutableStateFlow(loggedInUser).asStateFlow()
        }
        val loginAPI = mockk<LoginAPI>()
        val userServer = mockk<UserServer> {
            coEvery { deleteAddress(any()) } throws IOException()
        }

        val viewModel = AccountScreenViewModel(loginAPI, userRepository, userServer)
        viewModel.deleteAddress(address1)
        viewModel.deleteAddressRunning.first { !it }

        assertTrue(viewModel.networkError)
    }
}
