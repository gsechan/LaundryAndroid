package com.gabesechan.laundrydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.accountscreen.AccountScreen
import com.gabesechan.laundrydemo.drycleaningscreen.DryCleaningComposable
import com.gabesechan.laundrydemo.homescreen.HomeScreen
import com.gabesechan.laundrydemo.user.UserRepository
import com.gabesechan.laundrydemo.login.Login
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.orders.OrderScreen
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import com.gabesechan.laundrydemo.ui.widgets.DestinationScreen
import com.gabesechan.laundrydemo.ui.widgets.NavMenuScreen
import com.gabesechan.laundrydemo.user.User
import com.gabesechan.laundrydemo.washfoldscreen.WashFoldScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var loginAPI: LoginAPI

    private var isReady = MutableStateFlow(false)

    private var navItems = listOf(
        DestinationScreen("home", R.string.home, R.drawable.home, ::HomeScreen),
        DestinationScreen("wash", R.string.wash_fold, R.drawable.washer, ::WashFoldScreen),
        DestinationScreen("dryclean", R.string.dry_clean, R.drawable.dry_cleaning, ::DryCleaningComposable),
        DestinationScreen("orders", R.string.orders, R.drawable.order, ::OrderScreen),
        DestinationScreen("account", R.string.account, R.drawable.account, ::AccountScreen),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady.value }
        lifecycleScope.launch(Dispatchers.IO) {
            val token = userRepository.initFromDisk()
            //If we're logged in, check the auth with the server for expiry issues
            if(token != null) {
                loginAPI.checkAuth(token)
            }
            else {
                loginAPI.logout()
            }
            isReady.value = true
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val user = userRepository.current.collectAsState().value
            LaundryDemoTheme {
                if(user.isLoggedIn()) {
                    NavMenuScreen(navController, navItems) {
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                        ) {
                            navItems.forEach { item->
                                composable(item.route){ item.screen() }
                            }
                        }

                    }
                }
                else {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            Login()
                        }
                    }
                }
            }
        }
    }
}