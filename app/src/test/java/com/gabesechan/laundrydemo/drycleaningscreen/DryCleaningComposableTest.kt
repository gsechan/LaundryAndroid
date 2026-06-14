package com.gabesechan.laundrydemo.drycleaningscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.laundromatinfo.JSONItem
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
class DryCleaningComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val item1 = JSONItem("1", "Shirt", "10.00", "wash")
    private val item2 = JSONItem("2", "Pants", "5.50", "wash")
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
    ) {
        composeTestRule.setContent {
            DryCleaningComposableInner(
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
                navController = mockk<NavController>(relaxed = true)
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

        val cost1 = formatter.format(BigDecimal(item1.price) * BigDecimal(itemCounts[item1.id]!!))
        val cost2 = formatter.format(BigDecimal(item2.price) * BigDecimal(itemCounts[item2.id]!!))

        composeTestRule.onNodeWithText(cost1).assertIsDisplayed()
        composeTestRule.onNodeWithText(cost2).assertIsDisplayed()
    }

    @Test
    fun testTotalCostIsDisplayed() {
        setContent()

        var totalCost = BigDecimal(0)
        items.forEach { item ->
            totalCost = totalCost.plus(BigDecimal(item.price) * BigDecimal(itemCounts[item.id]!!))
        }

        composeTestRule.onNodeWithText(formatter.format(totalCost)).assertIsDisplayed()
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

    private fun getString(resId: Int): String {
        return androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId)
    }
}
