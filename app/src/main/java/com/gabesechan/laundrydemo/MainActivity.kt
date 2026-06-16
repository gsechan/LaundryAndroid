package com.gabesechan.laundrydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.gabesechan.laundrydemo.login.UserRepository
import com.gabesechan.laundrydemo.login.LoginAPI
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