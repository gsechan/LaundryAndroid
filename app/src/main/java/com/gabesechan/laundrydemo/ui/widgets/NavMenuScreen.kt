package com.gabesechan.laundrydemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class DestinationScreen(
    val route: String,
    @StringRes val text: Int,
    @DrawableRes val icon: Int,
    val screen: @Composable ()-> Unit
)

@Composable
fun NavMenuScreen(navController: NavController, items: List<DestinationScreen>, content: @Composable () -> Unit) {
    val curScreen = navController.currentDestination?.route
    val stackEntry by navController.currentBackStackEntryAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEach {
                    NavigationBarItem(
                        selected = stackEntry?.destination?.route == it.route,
                        onClick = {
                            navController.navigate(it.route)
                        },
                        icon = {
                            if(it.icon != 0) {
                                Icon(
                                    painter = painterResource(id = it.icon),
                                    contentDescription = stringResource(it.text),
                                    tint = MaterialTheme.colorScheme.onSurface
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
