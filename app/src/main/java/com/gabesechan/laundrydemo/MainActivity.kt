package com.gabesechan.laundrydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.accountscreen.AccountScreen
import com.gabesechan.laundrydemo.user.UserRepository
import com.gabesechan.laundrydemo.login.Login
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import com.gabesechan.laundrydemo.ui.widgets.DestinationScreen
import com.gabesechan.laundrydemo.ui.widgets.NavMenuScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var loginAPI: LoginAPI

    private var isReady = false

    private var navItems = listOf(
        DestinationScreen("home", R.string.home, 0, ::HomeScreen),
        DestinationScreen("wash", R.string.wash_fold, 0, ::WashFoldScreen),
        DestinationScreen("dryclean", R.string.dry_clean, 0, ::DryCleaningScreen),
        DestinationScreen("account", R.string.account, 0, ::AccountScreen),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch(Dispatchers.IO) {
            userRepository.initFromDisk()
            isReady = true
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

@Composable
fun HomeScreen() {
    Column {
        Text(
            text = "Home screen",
        )
    }
}

@Composable
fun WashFoldScreen() {
    Column {
        Text(
            text = "Wash and Fold",
        )
    }
}

@Composable
fun DryCleaningScreen() {
    Column {
        Text(
            text = "Dry Cleaning",
        )
    }
}

