package com.gabesechan.laundrydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.accountscreen.AccountScreen
import com.gabesechan.laundrydemo.drycleaningscreen.DryCleaningComposable
import com.gabesechan.laundrydemo.login.CreateAccountScreen
import com.gabesechan.laundrydemo.user.UserRepository
import com.gabesechan.laundrydemo.login.Login
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.orders.OrderScreen
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import com.gabesechan.laundrydemo.ui.widgets.DestinationScreen
import com.gabesechan.laundrydemo.ui.widgets.NavMenuScreen
import com.gabesechan.laundrydemo.user.AddAddressScreen
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady.value }
        lifecycleScope.launch(Dispatchers.IO) {
            loginAPI.useSavedLogin()
            isReady.value = true
        }
        enableEdgeToEdge()
        setContent {
            MainScreenComposable(userRepository)
        }
    }
}