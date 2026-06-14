package com.gabesechan.laundrydemo.orders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.models.Order
import com.gabesechan.laundrydemo.models.OrderLine
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class OrdersScreenComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(resId: Int, vararg formatArgs: Any): String {
        return ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId, *formatArgs)
    }

    private fun order(id: String, state: String, lines: List<OrderLine>) = Order(
        id = id,
        state = state,
        completed = null,
        lastChange = 0L,
        submitted = 0L,
        scheduledPickup = 0L,
        scheduledDropoff = 0L,
        pickupAddressId = "pickup",
        dropoffAddressId = "dropoff",
        lines = lines,
    )

    private val lineWithQuantity = OrderLine(
        itemType = "wash",
        name = "Shirt",
        pricePerUnit = "5.00",
        quantity = "3",
        totalCost = "15.00",
    )

    private val lineWithoutQuantity = OrderLine(
        itemType = "dryclean",
        name = "Suit",
        pricePerUnit = "20.00",
        quantity = null,
        totalCost = null,
    )

    private val lineWithoutTotalCost = OrderLine(
        itemType = "dryclean",
        name = "Coat",
        pricePerUnit = "25.00",
        quantity = "1",
        totalCost = null,
    )

    @Test
    fun testOrdersNotShownWhenNotLoaded() {
        val order1 = order("1", "pending", listOf(lineWithQuantity))

        composeTestRule.setContent {
            OrderScreenInternal(false, listOf(order1))
        }

        composeTestRule.onNodeWithText(string(R.string.order_num, "1")).assertDoesNotExist()
    }

    @Test
    fun testAllOrdersAreDisplayed() {
        val order1 = order("1", "pending", listOf(lineWithQuantity))
        val order2 = order("2", "completed", listOf(lineWithQuantity))

        composeTestRule.setContent {
            OrderScreenInternal(true, listOf(order1, order2))
        }

        composeTestRule.onNodeWithText(string(R.string.order_num, "1")).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.order_num, "2")).assertIsDisplayed()
    }

    @Test
    fun testOrderNumberStatusAndLinesAreDisplayed() {
        val order1 = order("1", "pending", listOf(lineWithQuantity, lineWithoutQuantity))

        composeTestRule.setContent {
            OrderScreenInternal(true, listOf(order1))
        }

        composeTestRule.onNodeWithText(string(R.string.order_num, "1")).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.status, "pending")).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            string(
                R.string.order_line_with_quantity,
                lineWithQuantity.quantity!!,
                lineWithQuantity.name,
                lineWithQuantity.pricePerUnit,
                lineWithQuantity.totalCost!!,
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            string(
                R.string.order_line_without_quantity,
                lineWithoutQuantity.name,
                lineWithoutQuantity.pricePerUnit,
            )
        ).assertIsDisplayed()
    }

    @Test
    fun testLineWithNullQuantityDisplaysWithoutQuantity() {
        val order1 = order("1", "pending", listOf(lineWithoutQuantity))

        composeTestRule.setContent {
            OrderScreenInternal(true, listOf(order1))
        }

        composeTestRule.onNodeWithText(
            string(
                R.string.order_line_without_quantity,
                lineWithoutQuantity.name,
                lineWithoutQuantity.pricePerUnit,
            )
        ).assertIsDisplayed()
    }

    @Test
    fun testLineWithNullTotalCostDisplaysWithoutQuantity() {
        val order1 = order("1", "pending", listOf(lineWithoutTotalCost))

        composeTestRule.setContent {
            OrderScreenInternal(true, listOf(order1))
        }

        composeTestRule.onNodeWithText(
            string(
                R.string.order_line_without_quantity,
                lineWithoutTotalCost.name,
                lineWithoutTotalCost.pricePerUnit,
            )
        ).assertIsDisplayed()
    }

    @Test
    fun testLineWithQuantityAndTotalCostDisplaysWithQuantity() {
        val order1 = order("1", "pending", listOf(lineWithQuantity))

        composeTestRule.setContent {
            OrderScreenInternal(true, listOf(order1))
        }

        composeTestRule.onNodeWithText(
            string(
                R.string.order_line_with_quantity,
                lineWithQuantity.quantity!!,
                lineWithQuantity.name,
                lineWithQuantity.pricePerUnit,
                lineWithQuantity.totalCost!!,
            )
        ).assertIsDisplayed()
    }
}
