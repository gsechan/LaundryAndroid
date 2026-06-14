package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
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
class NavMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val itemWithIcon = DestinationScreen("home", R.string.book_now, R.drawable.home) {}
    private val itemWithoutIcon = DestinationScreen("account", R.string.create_account, 0) {}

    @Test
    fun testItemIsDisplayedWithIconAndText() {
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            NavMenuScreen(navController, listOf(itemWithIcon)) {}
        }

        composeTestRule.onNodeWithText("Book Now").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NavItemIcon", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testItemWithoutIconDoesNotCrashAndShowsNoIcon() {
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            NavMenuScreen(navController, listOf(itemWithoutIcon)) {}
        }

        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NavItemIcon", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun testClickingItemNavigatesToItsRoute() {
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            NavMenuScreen(navController, listOf(itemWithIcon, itemWithoutIcon)) {}
        }

        composeTestRule.onNodeWithText("Create Account").performClick()

        verify(exactly = 1) { navController.navigate("account") }
    }

    @Test
    fun testContentIsDisplayed() {
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            NavMenuScreen(navController, emptyList()) {
                Text("Page Content")
            }
        }

        composeTestRule.onNodeWithText("Page Content").assertIsDisplayed()
    }
}
