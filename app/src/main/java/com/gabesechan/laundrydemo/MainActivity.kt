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
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabesechan.laundrydemo.account.User
import com.gabesechan.laundrydemo.account.UserRepository
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.ui.theme.LaundryDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
object Login
@Serializable
object Content

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var loginAPI: LoginAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val user = userRepository.current.collectAsState().value
            LaundryDemoTheme {
                if(user.isLoggedIn()) {
                    NavHost(navController = navController, startDestination = Content) {
                        composable<Content> { Content({ loginAPI.logout() }) }
                    }
                }
                else {
                    NavHost(navController = navController, startDestination = Login) {
                        composable<Login> {
                            Login(
                                {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val result = loginAPI.login("Gabe","pw")
                                        if(result is LoginAPI.LoginResult.NetworkError) {
                                            //update UI
                                        }
                                        else if(result is LoginAPI.LoginResult.LoginFailed) {
                                            //update UI
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }




}

@Composable
fun Content(logoutFunc: ()->Unit, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Content screen",
            modifier = modifier
        )
        Button(onClick = {
            logoutFunc()
        }) {
            Text("Click me!")
        }

    }


}

@Composable
fun Login(loginFunc: ()->Unit, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Login screen",
            modifier = modifier
        )
        Button(onClick = {
            loginFunc()
        })
        {
            Text("Click me!")
        }
    }
}
