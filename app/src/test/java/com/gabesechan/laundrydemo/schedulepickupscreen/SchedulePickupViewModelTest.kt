package com.gabesechan.laundrydemo.schedulepickupscreen

import androidx.lifecycle.SavedStateHandle
import com.gabesechan.laundrydemo.laundromatinfo.AvailableDateTime
import com.gabesechan.laundrydemo.laundromatinfo.AvailableTimesResponse
import com.gabesechan.laundrydemo.laundromatinfo.ItemsResponse
import com.gabesechan.laundrydemo.models.Item
import com.gabesechan.laundrydemo.laundromatinfo.LaundromatInfoServer
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import com.gabesechan.laundrydemo.network.NetworkResponse
import com.gabesechan.laundrydemo.models.Order
import com.gabesechan.laundrydemo.models.OrderAddress
import com.gabesechan.laundrydemo.orders.OrdersServer
import com.gabesechan.laundrydemo.orders.PostOrderResponse
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.orders.PostOrderLine
import com.gabesechan.laundrydemo.user.UserRepository
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
import kotlinx.coroutines.runBlocking
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
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class SchedulePickupViewModelTest {

    private val address = Address("addr1", "123 Main St", null, "Anytown", "ST", "US", "00000")
    private val user = User("gabe", "gabe@example.com", "1234567890", listOf(address))
    private val orderAddress = OrderAddress("123 Main St", null, "Anytown", "ST", "US", "00000")

    private val dryCleanItem = Item("1", "Shirt", BigDecimal("5.00"), "DRY_CLEANING")
    private val dryCleanItem2 = Item("3", "Pants", BigDecimal("7.00"), "DRY_CLEANING")
    private val washFoldItem = Item("2", "Wash and Fold", BigDecimal("10.00"), "WASH_AND_FOLD")
    private val washFoldItem2 = Item("4", "Bedding", BigDecimal("15.00"), "WASH_AND_FOLD")

    private val availableTimesResponse = AvailableTimesResponse(
        pickup = listOf(AvailableDateTime(1000L, listOf(TimeRange(0L, 3600000L)))),
        delivery = listOf(AvailableDateTime(2000L, listOf(TimeRange(0L, 3600000L)))),
        minTimeBetweenPickupAndDelivery = 500L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun savedStateHandle(itemType: String? = null): SavedStateHandle {
        return if (itemType == null) SavedStateHandle() else SavedStateHandle(mapOf("itemType" to itemType))
    }

    private fun userRepository(currentUser: User = user): UserRepository {
        return mockk<UserRepository> {
            every { current } returns MutableStateFlow(currentUser).asStateFlow()
        }
    }

    private fun awaitDataLoaded(viewModel: SchedulePickupViewModel) = runBlocking {
        viewModel.dataLoaded.first { it }
    }

    @Test
    fun testInitSuccessLoadsDryCleaningItemsAndEnablesPickupDates() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(
                true, null, emptyList(),
                ItemsResponse(listOf(dryCleanItem, washFoldItem))
            )
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        assertFalse(viewModel.dataError)
        assertEquals(listOf(dryCleanItem), viewModel.items)
        assertEquals(mapOf("1" to 0), viewModel.itemCounts.value)
        assertTrue(viewModel.pickupDateValues.value.selectableDates.isSelectableDate(1000L))
        assertFalse(viewModel.pickupDateValues.value.selectableDates.isSelectableDate(999L))
    }

    @Test
    fun testInitNetworkErrorSetsDataError() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } throws IOException()
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        assertTrue(viewModel.dataError)
    }

    @Test
    fun testSelectAddressUpdatesSelectedAddress() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val newAddress = Address("addr2", "456 Other St", null, "Othertown", "ST", "US", "11111")
        viewModel.selectAddress(newAddress)

        assertEquals(newAddress, viewModel.selectedAddress.value)
    }

    @Test
    fun testOnCountChangedUpdatesItemCounts() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        viewModel.onCountChanged("1", 3)

        assertEquals(mapOf("1" to 3), viewModel.itemCounts.value)
    }

    @Test
    fun testSetPickupDateAndTimeUpdatesDropoffSelectableDates() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        assertEquals(1000L, viewModel.pickupDateValues.value.curSelectedDate)
        assertEquals(availableTimesResponse.pickup[0].times, viewModel.pickupDateValues.value.selectableTimes)
        assertNull(viewModel.dropoffDateValues.value.curSelectedDate)

        val pickupTime = TimeRange(0L, 3600000L)
        viewModel.setPickupTime(pickupTime)
        assertEquals(pickupTime, viewModel.pickupDateValues.value.curSelectedTime)

        val earliest = 1000L + availableTimesResponse.minTimeBetweenPickupAndDelivery
        assertTrue(viewModel.dropoffDateValues.value.selectableDates.isSelectableDate(2000L))
        assertFalse(viewModel.dropoffDateValues.value.selectableDates.isSelectableDate(earliest - 1))
    }

    @Test
    fun testSetDropoffDateAndTime() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        viewModel.setDropoffDate(2000L)
        assertEquals(2000L, viewModel.dropoffDateValues.value.curSelectedDate)
        assertEquals(availableTimesResponse.delivery[0].times, viewModel.dropoffDateValues.value.selectableTimes)

        val dropoffTime = TimeRange(0L, 3600000L)
        viewModel.setDropoffTime(dropoffTime)
        assertEquals(dropoffTime, viewModel.dropoffDateValues.value.curSelectedTime)
    }

    @Test
    fun testBookSuccessSetsIsBeooked() = runTest {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val epoch = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
        val order = Order(
            "order1", "PENDING", null, epoch, epoch,
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
            orderAddress, orderAddress, emptyList()
        )
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } returns NetworkResponse(true, null, emptyList(), PostOrderResponse(order))
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))

        viewModel.book()

        assertTrue(viewModel.isBooked.value)
        assertFalse(viewModel.showBookingSpinner.value)
        assertFalse(viewModel.dataError)
    }

    @Test
    fun testBookNetworkErrorSetsDataError() = runTest {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } throws IOException()
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))

        viewModel.book()

        assertFalse(viewModel.isBooked.value)
        assertFalse(viewModel.showBookingSpinner.value)
        assertTrue(viewModel.dataError)
    }

    @Test
    fun testBookEnabledRequiresDropoffTimeAndNonZeroCounts() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.bookEnabled.value)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertTrue(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhenPickupDateNotSet() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertNull(viewModel.pickupDateValues.value.curSelectedDate)
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhenPickupTimeNotSet() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertNull(viewModel.pickupDateValues.value.curSelectedTime)
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhenDropoffDateNotSet() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertNull(viewModel.dropoffDateValues.value.curSelectedDate)
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhenAllCountsAreZero() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        advanceUntilIdle()

        assertTrue(viewModel.itemCounts.value.all { it.value == 0 })
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhileOrderPosting() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } coAnswers {
                kotlinx.coroutines.CompletableDeferred<NetworkResponse<PostOrderResponse>>().await()
            }
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertTrue(viewModel.bookEnabled.value)

        viewModel.book()
        advanceUntilIdle()

        assertTrue(viewModel.showBookingSpinner.value)
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookEnabledFalseWhenSelectedAddressIsNull() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val noAddressUser = User("gabe", "gabe@example.com", "1234567890", emptyList())
        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(noAddressUser), orderServer, SavedStateHandle())
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)
        advanceUntilIdle()

        assertNull(viewModel.selectedAddress.value)
        assertFalse(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testInitWithWashAndFoldItemTypeFiltersToWashAndFoldItems() {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(
                true, null, emptyList(),
                ItemsResponse(listOf(dryCleanItem, washFoldItem))
            )
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, savedStateHandle("WASH_AND_FOLD"))
        awaitDataLoaded(viewModel)

        assertFalse(viewModel.dataError)
        assertEquals(listOf(washFoldItem), viewModel.items)
        assertEquals(mapOf("2" to 0), viewModel.itemCounts.value)
    }

    @Test
    fun testBookEnabledTrueForWashAndFoldEvenWithZeroCounts() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(washFoldItem)))
        }
        val orderServer = mockk<OrdersServer>()

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, savedStateHandle("WASH_AND_FOLD"))
        awaitDataLoaded(viewModel)

        val job = launch { viewModel.bookEnabled.collect {} }
        advanceUntilIdle()

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        advanceUntilIdle()

        assertTrue(viewModel.itemCounts.value.all { it.value == 0 })
        assertTrue(viewModel.bookEnabled.value)

        job.cancel()
    }

    @Test
    fun testBookWithWashAndFoldPostsLineWithNullQuantityForEachItem() = runTest {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(washFoldItem)))
        }
        val epoch = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
        val order = Order(
            "order1", "PENDING", null, epoch, epoch,
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
            orderAddress, orderAddress, emptyList()
        )
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } returns NetworkResponse(true, null, emptyList(), PostOrderResponse(order))
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, savedStateHandle("WASH_AND_FOLD"))
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))

        viewModel.book()

        coVerify(exactly = 1) {
            orderServer.postOrder(match { it.order.lines == listOf(PostOrderLine("2", null)) })
        }
    }

    @Test
    fun testBookWithWashAndFoldPostsAllLinesWithNullQuantity() = runTest {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(washFoldItem, washFoldItem2)))
        }
        val epoch = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
        val order = Order(
            "order1", "PENDING", null, epoch, epoch,
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
            orderAddress, orderAddress, emptyList()
        )
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } returns NetworkResponse(true, null, emptyList(), PostOrderResponse(order))
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, savedStateHandle("WASH_AND_FOLD"))
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))

        viewModel.book()

        coVerify(exactly = 1) {
            orderServer.postOrder(match { it.order.lines.isNotEmpty() && it.order.lines.all { line -> line.quantity == null } })
        }
    }

    @Test
    fun testBookWithDryCleaningOnlyIncludesItemsWithPositiveCounts() = runTest {
        val laundromatInfoServer = mockk<LaundromatInfoServer> {
            coEvery { availableTimes() } returns NetworkResponse(true, null, emptyList(), availableTimesResponse)
            coEvery { items() } returns NetworkResponse(true, null, emptyList(), ItemsResponse(listOf(dryCleanItem, dryCleanItem2)))
        }
        val epoch = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
        val order = Order(
            "order1", "PENDING", null, epoch, epoch,
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
            orderAddress, orderAddress, emptyList()
        )
        val orderServer = mockk<OrdersServer> {
            coEvery { postOrder(any()) } returns NetworkResponse(true, null, emptyList(), PostOrderResponse(order))
        }

        val viewModel = SchedulePickupViewModel(laundromatInfoServer, userRepository(), orderServer, savedStateHandle("DRY_CLEANING"))
        awaitDataLoaded(viewModel)

        viewModel.setPickupDate(1000L)
        viewModel.setPickupTime(TimeRange(0L, 3600000L))
        viewModel.setDropoffDate(2000L)
        viewModel.setDropoffTime(TimeRange(0L, 3600000L))
        viewModel.onCountChanged("1", 2)

        viewModel.book()

        coVerify(exactly = 1) {
            orderServer.postOrder(match { it.order.lines == listOf(PostOrderLine("1", "2")) })
        }
    }
}
