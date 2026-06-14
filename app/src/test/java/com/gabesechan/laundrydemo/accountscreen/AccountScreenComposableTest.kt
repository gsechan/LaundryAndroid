package com.gabesechan.laundrydemo.accountscreen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
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
class AccountScreenComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val address1 = Address("1", "123 Main St", null, "Springfield", "IL", "USA", "62701")
    private val address2 = Address("2", "456 Oak Ave", "Apt 2", "Springfield", "IL", "USA", "62702")

    private val userWithAddresses = User(
        name = "Jane Doe",
        email = "jane@example.com",
        phone = "555-1234",
        addresses = listOf(address1, address2)
    )

    private val userWithoutAddresses = User(
        name = "Jane Doe",
        email = "jane@example.com",
        phone = "555-1234",
        addresses = emptyList()
    )

    private fun string(resId: Int, vararg formatArgs: Any): String {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        return context.getString(resId, *formatArgs)
    }

    @Test
    fun testNameEmailAndPhoneAreDisplayed() {
        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {})
        }

        composeTestRule.onNodeWithText("Jane Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.email_label, "jane@example.com")).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.phone_label, "555-1234")).assertIsDisplayed()
    }

    @Test
    fun testEmailLabelDisplayedAsEmptyWhenEmailIsNull() {
        val userWithoutEmail = userWithAddresses.copy(email = null)

        composeTestRule.setContent {
            AccountScreenInner(userWithoutEmail) {}
        }

        composeTestRule.onNodeWithText(string(R.string.email_label, "")).assertIsDisplayed()
    }

    @Test
    fun testAddressesHeaderAndAddressesAreDisplayedWhenUserHasAddresses() {
        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {})
        }

        composeTestRule.onNodeWithText(string(R.string.addresses)).assertIsDisplayed()
        composeTestRule.onNodeWithText("123 Main St").assertIsDisplayed()
        composeTestRule.onNodeWithText("456 Oak Ave").assertIsDisplayed()
    }

    @Test
    fun testAddressesHeaderIsNotDisplayedWhenUserHasNoAddresses() {
        composeTestRule.setContent {
            AccountScreenInner(userWithoutAddresses, {}, {})
        }

        composeTestRule.onNodeWithText(string(R.string.addresses)).assertDoesNotExist()
    }

    @Test
    fun testLogoutButtonIsDisplayed() {
        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {})
        }

        composeTestRule.onNodeWithText(string(R.string.logout)).assertIsDisplayed()
    }

    @Test
    fun testClickingLogoutButtonCallsLogoutClicked() {
        val logoutClicked = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, logoutClicked, {})
        }

        composeTestRule.onNodeWithText(string(R.string.logout)).performClick()

        verify(exactly = 1) { logoutClicked() }
    }

    @Test
    fun testDeleteButtonsAreDisplayedForEachAddress() {
        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {})
        }

        composeTestRule.onAllNodesWithContentDescription(string(R.string.delete_address))
            .assertCountEquals(2)
    }

    @Test
    fun testClickingDeleteButtonCallsOnDeleteAddressWithCorrectAddress() {
        val onDeleteAddress = mockk<(Address) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, onDeleteAddress)
        }

        composeTestRule.onAllNodesWithContentDescription(string(R.string.delete_address))[0].performClick()

        verify(exactly = 1) { onDeleteAddress(address1) }
    }

    @Test
    fun testEditButtonsAreDisplayedForEachAddress() {
        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {})
        }

        composeTestRule.onAllNodesWithContentDescription(string(R.string.edit_address))
            .assertCountEquals(2)
    }

    @Test
    fun testClickingEditButtonCallsOnEditAddressWithCorrectAddress() {
        val onEditAddress = mockk<(Address) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            AccountScreenInner(userWithAddresses, {}, {}, onEditAddress)
        }

        composeTestRule.onAllNodesWithContentDescription(string(R.string.edit_address))[0].performClick()

        verify(exactly = 1) { onEditAddress(address1) }
    }
}
