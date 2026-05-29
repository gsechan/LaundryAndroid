package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun Login(viewModel: LoginViewModel = hiltViewModel()) {
    LoginInner(viewModel::onLoginClicked)
}

@Composable
fun LoginInner(loginFunc: ()->Unit, modifier: Modifier = Modifier) {
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
