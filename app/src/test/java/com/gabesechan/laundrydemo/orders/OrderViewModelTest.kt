package com.gabesechan.laundrydemo.orders

import com.gabesechan.laundrydemo.models.Order
import com.gabesechan.laundrydemo.models.OrderAddress
import com.gabesechan.laundrydemo.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OrderViewModelTest {

    private val epoch = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)

    private val address = OrderAddress("123 Main St", null, "Anytown", "ST", "US", "00000")

    private fun order(
        id: String,
        state: String,
        submitted: Long
    ): Order {
        val submittedTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(submitted), ZoneOffset.UTC)
        return Order(
            id, state, null, submittedTime, submittedTime, epoch, epoch, address, address, emptyList()
        )
    }

    private fun awaitLoaded(viewModel: OrderViewModel) = runBlocking {
        viewModel.isLoaded.first { it }
    }

    @Test
    fun testInitSortsInProgressOrdersBeforeCompletedOrders() {
        val completed = order("1", "COMPLETED", 1000L)
        val inProgress = order("2", "PENDING", 500L)

        val ordersServer = mockk<OrdersServer> {
            coEvery { getAll() } returns NetworkResponse(true, null, emptyList(), listOf(completed, inProgress))
        }

        val viewModel = OrderViewModel(ordersServer)
        awaitLoaded(viewModel)

        assertEquals(listOf(inProgress, completed), viewModel.sortedOrders)
    }

    @Test
    fun testInitSortsOrdersWithinGroupByMostRecentlySubmittedFirst() {
        val older = order("1", "PENDING", 500L)
        val newer = order("2", "PENDING", 1500L)

        val ordersServer = mockk<OrdersServer> {
            coEvery { getAll() } returns NetworkResponse(true, null, emptyList(), listOf(older, newer))
        }

        val viewModel = OrderViewModel(ordersServer)
        awaitLoaded(viewModel)

        assertEquals(listOf(newer, older), viewModel.sortedOrders)
    }

    @Test
    fun testInitWithEmptyOrdersSetsIsLoadedAndEmptySortedOrders() {
        val ordersServer = mockk<OrdersServer> {
            coEvery { getAll() } returns NetworkResponse(true, null, emptyList(), emptyList())
        }

        val viewModel = OrderViewModel(ordersServer)
        awaitLoaded(viewModel)

        assertTrue(viewModel.sortedOrders.isEmpty())
    }
}
