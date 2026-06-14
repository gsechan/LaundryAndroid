package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.models.Address
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class AddressPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val address1 = Address("1", "123 Main St", null, "Springfield", "IL", "USA", "62701")
    private val address2 = Address("2", "456 Oak Ave", "Apt 2", "Springfield", "IL", "USA", "62702")

    @Test
    fun testSelectedAddressIsDisplayed() {
        composeTestRule.setContent {
            AddressPicker(
                addresses = listOf(address1, address2),
                selectedAddress = address1,
                onSelection = {},
                navController = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithText("123 Main St").assertIsDisplayed()
    }

    @Test
    fun testClickingFieldShowsAddNewAddress() {
        composeTestRule.setContent {
            AddressPicker(
                addresses = listOf(address1, address2),
                selectedAddress = null,
                onSelection = {},
                navController = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag("PickerTextField").performClick()

        composeTestRule.onNodeWithText("Add new address").assertIsDisplayed()
    }

    @Test
    fun testClickingAddressCallsOnSelectionWithThatAddress() {
        val onSelection = mockk<(Address) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            AddressPicker(
                addresses = listOf(address1, address2),
                selectedAddress = null,
                onSelection = onSelection,
                navController = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag("PickerTextField").performClick()
        composeTestRule.onNodeWithText("456 Oak Ave").performClick()

        verify(exactly = 1) { onSelection(address2) }
    }

    @Test
    fun testClickingAddNewAddressNavigatesToAddAddress() {
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            AddressPicker(
                addresses = listOf(address1, address2),
                selectedAddress = null,
                onSelection = {},
                navController = navController
            )
        }

        composeTestRule.onNodeWithTag("PickerTextField").performClick()
        composeTestRule.onNodeWithText("Add new address").performClick()

        verify(exactly = 1) { navController.navigate("addAddress") }
    }
}
