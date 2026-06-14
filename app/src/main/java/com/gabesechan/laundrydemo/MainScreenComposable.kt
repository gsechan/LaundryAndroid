package com.gabesechan.laundrydemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gabesechan.laundrydemo.accountscreen.AccountScreen
import com.gabesechan.laundrydemo.schedulepickupscreen.SchedulePickupComposable
import com.gabesechan.laundrydemo.login.CreateAccountScreen
import com.gabesechan.laundrydemo.login.Login
import com.gabesechan.laundrydemo.orders.OrderScreen
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import com.gabesechan.laundrydemo.ui.widgets.DestinationScreen
import com.gabesechan.laundrydemo.ui.widgets.NavMenuScreen
import com.gabesechan.laundrydemo.user.AddEditAddressScreen
import com.gabesechan.laundrydemo.models.User
import com.gabesechan.laundrydemo.user.UserRepository

private var navItems = listOf(
    DestinationScreen("pickup/WASH_AND_FOLD", R.string.wash_fold, R.drawable.washer, ::SchedulePickupComposable),
    DestinationScreen("pickup/DRY_CLEANING", R.string.dry_clean, R.drawable.dry_cleaning, ::SchedulePickupComposable),
    DestinationScreen("orders", R.string.orders, R.drawable.order, ::OrderScreen),
    DestinationScreen("account", R.string.account, R.drawable.account, ::AccountScreen),
)

@Composable
fun MainScreenComposable(
    userRepository: UserRepository,
    navController: NavHostController = rememberNavController(),
    loggedInContent: @Composable (NavHostController) -> Unit = { nc -> LoggedInContent(nc, navItems) },
    loggedOutContent: @Composable (NavHostController) -> Unit = { nc -> LoggedOutContent(nc) },
) {
    val user = userRepository.current.collectAsState().value
    MainScreenComposableInner(user, navController, loggedInContent, loggedOutContent)
}

@Composable
fun LoggedInContent(navController: NavHostController, navItems: List<DestinationScreen>) {
    NavMenuScreen(navController, navItems) {
        NavHost(
            navController = navController,
            startDestination = "pickup/WASH_AND_FOLD",
        ) {
            navItems.forEach { item->
                val arguments = if (item.route.startsWith("pickup/")) {
                    listOf(navArgument("itemType") {
                        type = NavType.StringType
                        defaultValue = item.route.removePrefix("pickup/")
                    })
                } else {
                    emptyList()
                }
                composable(item.route, arguments = arguments){ item.screen(navController) }
            }
            composable(
                "editAddress/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                AddEditAddressScreen(navController)
            }
            composable(
                "editAddress/new",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                    defaultValue = "new"
                })
            ) {
                AddEditAddressScreen(navController)
            }
        }
    }
}

@Composable
fun LoggedOutContent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(navController)
        }
        composable("createAccount") {
            CreateAccountScreen()
        }
    }
}

@Composable
fun MainScreenComposableInner(
    user: User,
    navController: NavHostController,
    loggedInContent: @Composable (NavHostController) -> Unit,
    loggedOutContent: @Composable (NavHostController) -> Unit,
) {
    LaundryDemoTheme() {
        if(user.isLoggedIn()) {
            loggedInContent(navController)
        }
        else {
            loggedOutContent(navController)
        }
    }
}