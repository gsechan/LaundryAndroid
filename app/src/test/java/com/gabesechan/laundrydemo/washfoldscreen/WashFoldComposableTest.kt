package com.gabesechan.laundrydemo.washfoldscreen

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
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
class WashFoldComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val washPrice = BigDecimal("2.50")
    private val formatter = NumberFormat.getCurrencyInstance()

    private val emptyDateTimeValues = DateTimePickerValues(
        selectableDates = DatePickerDefaults.AllDates,
        curSelectedDate = null,
        selectableTimes = emptyList(),
        curSelectedTime = null
    )

    private val pickedTimeDateTimeValues = emptyDateTimeValues.copy(
        curSelectedDate = 0L,
        curSelectedTime = TimeRange(0L, 3600000L),
    )

    private val emptyCallbacks = DateTimePickerCallbacks(
        onDateSelected = {},
        onTimeRangeSelected = {}
    )

    private fun string(resId: Int, vararg formatArgs: Any): String {
        return ApplicationProvider.getApplicationContext<android.content.Context>().getString(resId, *formatArgs)
    }

    private fun setContent(
        pickup: DateTimePickerValues = emptyDateTimeValues,
        onBook: () -> Unit = {},
        bookEnabled: Boolean = true,
        showBookingSpinner: Boolean = false,
    ) {
        composeTestRule.setContent {
            WashFoldScreenInner(
                addresses = emptyList(),
                selectedAddress = null,
                onAddressSelected = {},
                pickup = pickup,
                pickupCallbacks = emptyCallbacks,
                dropoff = emptyDateTimeValues,
                dropoffCallbacks = emptyCallbacks,
                washFoldPrice = washPrice,
                onBook = onBook,
                bookEnabled = bookEnabled,
                showBookingSpinner = showBookingSpinner,
                navController = mockk<NavController>(relaxed = true)
            )
        }
    }

    @Test
    fun testHeaderPricingTextIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.expected_wash_price, formatter.format(washPrice))).assertIsDisplayed()
    }

    @Test
    fun testAddressPickerIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.select_address)).assertIsDisplayed()
    }

    @Test
    fun testPickupDateTimePickerIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.pickup_select)).assertIsDisplayed()
    }

    @Test
    fun testDropoffPickerNotDisplayedWhenPickupTimeNotSelected() {
        setContent(pickup = emptyDateTimeValues)

        composeTestRule.onNodeWithText(string(R.string.dropoff_select)).assertDoesNotExist()
    }

    @Test
    fun testDropoffPickerDisplayedWhenPickupTimeSelected() {
        setContent(pickup = pickedTimeDateTimeValues)

        composeTestRule.onNodeWithText(string(R.string.dropoff_select)).assertIsDisplayed()
    }

    @Test
    fun testBookNowButtonIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(string(R.string.book_now)).assertIsDisplayed()
    }

    @Test
    fun testBookNowEnabledWhenBookEnabledIsTrue() {
        setContent(bookEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsEnabled()
    }

    @Test
    fun testBookNowDisabledWhenBookEnabledIsFalse() {
        setContent(bookEnabled = false)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").assertIsNotEnabled()
    }

    @Test
    fun testSpinnerShownOnlyWhenShowBookingSpinnerIsTrue() {
        setContent(showBookingSpinner = true)

        composeTestRule.onNodeWithTag("LoadingProgress").assertExists()
    }

    @Test
    fun testSpinnerNotShownWhenShowBookingSpinnerIsFalse() {
        setContent(showBookingSpinner = false)

        composeTestRule.onNodeWithTag("LoadingProgress").assertDoesNotExist()
    }

    @Test
    fun testClickingBookNowCallsOnBookWhenEnabled() {
        val onBook = mockk<() -> Unit>(relaxed = true)
        setContent(onBook = onBook, bookEnabled = true)

        composeTestRule.onNodeWithTag("LoadingButtonRoot").performClick()

        verify(exactly = 1) { onBook() }
    }
}
