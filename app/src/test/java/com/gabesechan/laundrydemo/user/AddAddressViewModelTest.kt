package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshots.Snapshot
import com.gabesechan.laundrydemo.login.LoginAddress
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddAddressViewModelTest {

    private val user = User("gabe", "gabe@example.com", "1234567890", emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setText(state: TextFieldState, text: String) {
        state.setTextAndPlaceCursorAtEnd(text)
        Snapshot.sendApplyNotifications()
    }

    private fun userRepository(currentUser: User = user): UserRepository {
        return mockk<UserRepository> {
            every { current } returns MutableStateFlow(currentUser).asStateFlow()
            every { authToken } returns "token"
            coEvery { setUser(any(), any()) } returns Unit
        }
    }

    private fun fillRequiredFields(viewModel: AddAddressViewModel) {
        setText(viewModel.street1, "123 Main St")
        setText(viewModel.country, "US")
        setText(viewModel.city, "Anytown")
        setText(viewModel.state, "ST")
        setText(viewModel.postcode, "00000")
    }

    @Test
    fun testCreateEnabledFalseInitially() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = AddAddressViewModel(mockk(), userRepository())

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledTrueWhenRequiredFieldsFilled() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = AddAddressViewModel(mockk(), userRepository())

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        fillRequiredFields(viewModel)
        advanceUntilIdle()

        assertTrue(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledFalseWhenRequiredFieldMissing() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = AddAddressViewModel(mockk(), userRepository())

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        setText(viewModel.street1, "123 Main St")
        setText(viewModel.country, "US")
        setText(viewModel.city, "Anytown")
        setText(viewModel.state, "ST")
        advanceUntilIdle()

        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledFalseWhilePosting() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val userServer = mockk<UserServer> {
            coEvery { addAddress(any()) } coAnswers {
                kotlinx.coroutines.CompletableDeferred<NetworkResponse<PostAddressResponse>>().await()
            }
        }
        val viewModel = AddAddressViewModel(userServer, userRepository())

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        fillRequiredFields(viewModel)
        advanceUntilIdle()

        assertTrue(viewModel.createEnabled.value)

        viewModel.addAccountClicked()
        advanceUntilIdle()

        assertTrue(viewModel.addRunning.value)
        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testAddAccountClickedSuccessUpdatesUserAndEmitsNavEvent() = runTest {
        val newAddress = LoginAddress("addr2", "123 Main St", null, "Anytown", "ST", "US", "00000")
        val userServer = mockk<UserServer> {
            coEvery { addAddress(any()) } returns NetworkResponse(
                true, null, emptyList(), PostAddressResponse(newAddress)
            )
        }
        val userRepository = userRepository()
        val viewModel = AddAddressViewModel(userServer, userRepository)

        fillRequiredFields(viewModel)

        viewModel.addAccountClicked()
        viewModel.addRunning.first { !it }

        assertFalse(viewModel.networkError)
        assertEquals(Unit, viewModel.navEvent.first())
        coVerify(exactly = 1) {
            userRepository.setUser(
                match { it.addresses == listOf(Address("addr2", "123 Main St", null, "Anytown", "ST", "US", "00000")) },
                "token"
            )
        }
    }

    @Test
    fun testAddAccountClickedNetworkErrorSetsNetworkError() = runTest {
        val userServer = mockk<UserServer> {
            coEvery { addAddress(any()) } throws IOException()
        }
        val viewModel = AddAddressViewModel(userServer, userRepository())

        fillRequiredFields(viewModel)

        viewModel.addAccountClicked()
        viewModel.addRunning.first { !it }

        assertTrue(viewModel.networkError)
    }
}
