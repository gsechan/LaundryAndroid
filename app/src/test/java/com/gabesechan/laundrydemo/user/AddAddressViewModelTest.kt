package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import com.gabesechan.laundrydemo.login.UserRepository
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    private fun savedStateHandle(id: String? = null): SavedStateHandle {
        return if (id == null) SavedStateHandle() else SavedStateHandle(mapOf("id" to id))
    }

    private fun userRepository(currentUser: User = user): UserRepository {
        return mockk<UserRepository> {
            every { current } returns MutableStateFlow(currentUser).asStateFlow()
            every { setUser(any()) } returns Unit
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
        val viewModel = AddAddressViewModel(mockk(), userRepository(), savedStateHandle())

        val job = launch { viewModel.createEnabled.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.createEnabled.value)

        job.cancel()
    }

    @Test
    fun testCreateEnabledTrueWhenRequiredFieldsFilled() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = AddAddressViewModel(mockk(), userRepository(), savedStateHandle())

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
        val viewModel = AddAddressViewModel(mockk(), userRepository(), savedStateHandle())

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
        val viewModel = AddAddressViewModel(userServer, userRepository(), savedStateHandle())

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
        val newAddress = Address("addr2", "123 Main St", null, "Anytown", "ST", "US", "00000")
        val userServer = mockk<UserServer> {
            coEvery { addAddress(any()) } returns NetworkResponse(
                true, null, emptyList(), PostAddressResponse(newAddress)
            )
        }
        val userRepository = userRepository()
        val viewModel = AddAddressViewModel(userServer, userRepository, savedStateHandle())

        fillRequiredFields(viewModel)

        viewModel.addAccountClicked()
        viewModel.addRunning.first { !it }

        assertFalse(viewModel.networkError)
        assertEquals(Unit, viewModel.navEvent.first())
        verify(exactly = 1) {
            userRepository.setUser(
                match { it.addresses == listOf(Address("addr2", "123 Main St", null, "Anytown", "ST", "US", "00000")) }
            )
        }
    }

    @Test
    fun testAddAccountClickedNetworkErrorSetsNetworkError() = runTest {
        val userServer = mockk<UserServer> {
            coEvery { addAddress(any()) } throws IOException()
        }
        val viewModel = AddAddressViewModel(userServer, userRepository(), savedStateHandle())

        fillRequiredFields(viewModel)

        viewModel.addAccountClicked()
        viewModel.addRunning.first { !it }

        assertTrue(viewModel.networkError)
    }

    @Test
    fun testInitWithExistingIdPrefillsFields() = runTest {
        val existingAddress = Address("addr1", "123 Main St", "Apt 4", "Anytown", "ST", "US", "00000")
        val userWithAddress = user.copy(addresses = listOf(existingAddress))
        val viewModel = AddAddressViewModel(mockk(), userRepository(userWithAddress), savedStateHandle("addr1"))

        assertEquals("123 Main St", viewModel.street1.text.toString())
        assertEquals("Apt 4", viewModel.street2.text.toString())
        assertEquals("Anytown", viewModel.city.text.toString())
        assertEquals("ST", viewModel.state.text.toString())
        assertEquals("US", viewModel.country.text.toString())
        assertEquals("00000", viewModel.postcode.text.toString())
    }

    @Test
    fun testAddAccountClickedWithExistingIdCallsUpdateAddress() = runTest {
        val existingAddress = Address("addr1", "123 Main St", null, "Anytown", "ST", "US", "00000")
        val updatedAddress = existingAddress.copy(street1 = "456 Oak Ave")
        val userWithAddress = user.copy(addresses = listOf(existingAddress))
        val userServer = mockk<UserServer> {
            coEvery { updateAddress("addr1", any()) } returns NetworkResponse(
                true, null, emptyList(), PostAddressResponse(updatedAddress)
            )
        }
        val userRepository = userRepository(userWithAddress)
        val viewModel = AddAddressViewModel(userServer, userRepository, savedStateHandle("addr1"))

        setText(viewModel.street1, "456 Oak Ave")

        viewModel.addAccountClicked()
        viewModel.addRunning.first { !it }

        assertFalse(viewModel.networkError)
        assertEquals(Unit, viewModel.navEvent.first())
        coVerify(exactly = 1) { userServer.updateAddress("addr1", any()) }
        verify(exactly = 1) {
            userRepository.setUser(match { it.addresses == listOf(updatedAddress) })
        }
    }
}
