package com.gabesechan.laundrydemo

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.user.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class MainScreenComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loggedInUser = User("Jane Doe", "jane@example.com", "555-1234", emptyList())

    private fun setContent(user: User) {
        composeTestRule.setContent {
            MainScreenComposableInner(
                user = user,
                navController = rememberNavController(),
                loggedInContent = { Text("WashFoldContent") },
                loggedOutContent = { Text("LoginContent") },
            )
        }
    }

    @Test
    fun testLoggedOutUserSeesLoginContent() {
        setContent(User.NoUser)

        composeTestRule.onNodeWithText("LoginContent").assertIsDisplayed()
        composeTestRule.onNodeWithText("WashFoldContent").assertDoesNotExist()
    }

    @Test
    fun testLoggedInUserSeesWashFoldContent() {
        setContent(loggedInUser)

        composeTestRule.onNodeWithText("WashFoldContent").assertIsDisplayed()
        composeTestRule.onNodeWithText("LoginContent").assertDoesNotExist()
    }
}
