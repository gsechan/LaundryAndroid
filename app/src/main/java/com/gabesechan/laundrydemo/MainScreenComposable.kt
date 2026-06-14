package com.gabesechan.laundrydemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.accountscreen.AccountScreen
import com.gabesechan.laundrydemo.drycleaningscreen.DryCleaningComposable
import com.gabesechan.laundrydemo.login.CreateAccountScreen
import com.gabesechan.laundrydemo.login.Login
import com.gabesechan.laundrydemo.orders.OrderScreen
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import com.gabesechan.laundrydemo.ui.widgets.DestinationScreen
import com.gabesechan.laundrydemo.ui.widgets.NavMenuScreen
import com.gabesechan.laundrydemo.user.AddAddressScreen
import com.gabesechan.laundrydemo.user.User
import com.gabesechan.laundrydemo.user.UserRepository
import com.gabesechan.laundrydemo.washfoldscreen.WashFoldScreen

private var navItems = listOf(
    DestinationScreen("wash", R.string.wash_fold, R.drawable.washer, ::WashFoldScreen),
    DestinationScreen("dryclean", R.string.dry_clean, R.drawable.dry_cleaning, ::DryCleaningComposable),
    DestinationScreen("orders", R.string.orders, R.drawable.order, ::OrderScreen),
    DestinationScreen("account", R.string.account, R.drawable.account, ::AccountScreen),
)

@Composable
fun MainScreenComposable(
    userRepository: UserRepository,
    navController: NavHostController = rememberNavController(),
    loggedInContent: @Composable (NavHostController) -> Unit = { nc ->
        NavMenuScreen(nc, navItems) {
            NavHost(
                navController = nc,
                startDestination = "wash",
            ) {
                navItems.forEach { item->
                    composable(item.route){ item.screen(nc) }
                }
                composable("addAddress") {
                    AddAddressScreen(nc)
                }
            }
        }
    },
    loggedOutContent: @Composable (NavHostController) -> Unit = { nc ->
        NavHost(navController = nc, startDestination = "login") {
            composable("login") {
                Login(nc)
            }
            composable("createAccount") {
                CreateAccountScreen()
            }
        }
    },
) {
    val user = userRepository.current.collectAsState().value
    MainScreenComposableInner(user, navController, loggedInContent, loggedOutContent)
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