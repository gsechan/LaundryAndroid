package com.gabesechan.laundrydemo.schedulepickupscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.models.Item
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerCallbacks
import com.gabesechan.laundrydemo.ui.widgets.DateTimePickerValues
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.math.BigDecimal
import java.text.NumberFormat

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class SchedulePickupComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val item1 = Item("1", "Shirt", BigDecimal("10.00"), "wash")
    private val item2 = Item("2", "Pants", BigDecimal("5.50"), "wash")
    private val items = listOf(item1, item2)
    private val itemCounts = mapOf("1" to 2, "2" to 3)

    private val emptyDateTimeValues = DateTimePickerValues(
        selectableDates = DatePickerDefaults.AllDates,
        curSelectedDate = null,
        selectableTimes = emptyList(),
        curSelectedTime = null
    )

    private val emptyCallbacks = DateTimePickerCallbacks(
        onDateSelected = {},
        onTimeRangeSelected = {}
    )

    private val formatter = NumberFormat.getCurrencyInstance()

    private fun setContent(
        onBook: () -> Unit = {},
        pickup: DateTimePickerValues = emptyDateTimeValues,
        buttonEnabled: Boolean = true,
        isBooked: Boolean = false,
        dataError: Boolean = false,
        isLoaded: Boolean = true,
        items: List<Item> = this.items,
        itemCounts: Map<String, Int> = this.itemCounts,
        pricingComposable: @Composable (List<Item>, Map<String, Int>, (String, Int) -> Unit) -> Unit = ::DryCleanPricingComposable,
    ) {
        composeTestRule.setContent {
            SchedulePickupInner(
                isBooked = isBooked,
                dataError = dataError,
                isLoaded = isLoaded,
                addresses = emptyList(),
                selectedAddress = null,
                onAddressSelected = {},
                pickup = pickup,
                pickupCallbacks = emptyCallbacks,
                dropoff = emptyDateTimeValues,
                dropoffCallbacks = emptyCallbacks,
                onBook = onBook,
                itemCounts = itemCounts,
                onCountChanged = { _, _ -> },
                items = items,
                buttonEnabled = buttonEnabled,
                showBookingSpinner = false,
                navController = mockk<NavController>(relaxed = true),
                pricingComposable = pricingComposable,
            )
        }
    }

    @Test
    fun testEachItemIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText("Shirt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pants").assertIsDisplayed()
    }

    @Test
    fun testEachItemHasFormattedCostString() {
        setContent()

        val cost1 = formatter.format(item1.price * BigDecimal(itemCounts[item1.id]!!))
        val cost2 = formatter.format(item2.price * BigDecimal(itemCounts[item2.id]!!))

        composeTestRule.onNodeWithText(cost1).assertIsDisplayed()
        composeTestRule.onNodeWithText(cost2).assertIsDisplayed()
    }

    @Test
    fun testTotalCostIsDisplayed() {
        setContent()

        var totalCost = BigDecimal(0)
        items.forEach { item ->
            totalCost = totalCost.plus(item.price * BigDecimal(itemCounts[item.id]!!))
        }

        composeTestRule.onNodeWithText(formatter.format(totalCost)).assertIsDisplayed()
    }

    @Test
    fun testEachItemHasNumberPickerForDryCleaning() {
        setContent()

        composeTestRule.onAllNodesWithTag("IncrementButton").assertCountEquals(2)
        composeTestRule.onAllNodesWithTag("DecrementButton").assertCountEquals(2)
    }

    @Test
    fun testWashFoldShowsExpectedPriceMessage() {
        setContent(
            items = listOf(item1),
            itemCounts = mapOf(item1.id to 0),
            pricingComposable = ::WashFoldPricingComposable,
        )

        val expectedText = getString(R.string.expected_wash_price, formatter.format(item1.price))
        composeTestRule.onNodeWithText(expectedText).assertIsDisplayed()
    }

    @Test
    fun testWashFoldDoesNotShowNumberPickerOrTotalCost() {
        setContent(
            items = listOf(item1),
            itemCounts = mapOf(item1.id to 0),
            pricingComposable = ::WashFoldPricingComposable,
        )

        composeTestRule.onNodeWithTag("IncrementButton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("DecrementButton").assertDoesNotExist()
        composeTestRule.onNodeWithText("Total price:").assertDoesNotExist()
    }

    @Test
    fun testAddressPickerIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(getString(R.string.select_address)).assertIsDisplayed()
    }

    @Test
    fun testPickupDateTimePickerIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(getString(R.string.pickup_select)).assertIsDisplayed()
    }

    @Test
    fun testDropoffPickerNotDisplayedWhenPickupTimeNotSelected() {
        setContent()

        composeTestRule.onNodeWithText(getString(R.string.dropoff_select)).assertDoesNotExist()
    }

    @Test
    fun testLoadingButtonIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsDisplayed()
    }

    @Test
    fun testLoadingButtonEnabledWhenButtonEnabledIsTrue() {
        setContent(buttonEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsEnabled()
    }

    @Test
    fun testLoadingButtonDisabledWhenButtonEnabledIsFalse() {
        setContent(buttonEnabled = false)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsNotEnabled()
    }

    @Test
    fun testClickingLoadingButtonCallsOnBook() {
        val onBook = mockk<() -> Unit>(relaxed = true)
        setContent(onBook = onBook, buttonEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 1) { onBook() }
    }

    @Test
    fun testOrderBookedMessageShownWhenIsBooked() {
        setContent(isBooked = true)

        composeTestRule.onNodeWithText(getString(R.string.order_booked)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Shirt").assertDoesNotExist()
    }

    @Test
    fun testNetworkErrorShownWhenDataError() {
        setContent(dataError = true)

        composeTestRule.onNodeWithText(getString(R.string.network_error)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Shirt").assertDoesNotExist()
    }

    @Test
    fun testNothingShownWhenNotLoaded() {
        setContent(isLoaded = false)

        composeTestRule.onNodeWithText("Shirt").assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.order_booked)).assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.network_error)).assertDoesNotExist()
    }

    private fun getString(resId: Int, vararg formatArgs: Any): String {
        return androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId, *formatArgs)
    }
}
