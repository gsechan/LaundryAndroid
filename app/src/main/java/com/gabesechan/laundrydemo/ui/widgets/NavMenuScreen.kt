package com.gabesechan.laundrydemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

data class DestinationScreen(
    val route: String,
    @StringRes val text: Int,
    @DrawableRes val icon: Int,
    val screen: @Composable ()-> Unit
)

@Composable
fun NavMenuScreen(navController: NavController, items: List<DestinationScreen>, content: @Composable () -> Unit) {
    var curScreen = navController.currentDestination?.route
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEach {
                    NavigationBarItem(
                        selected = curScreen == it.route,
                        onClick = {
                            navController.navigate(it.route)
                        },
                        icon = {
                            if(it.icon != 0) {
                                Icon(
                                    painter = painterResource(id = it.icon),
                                    contentDescription = stringResource(it.text)
                                )
                            }
                        },
                        label = {
                            Text(stringResource(it.text))
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()){
            content()
        }
    }

}
