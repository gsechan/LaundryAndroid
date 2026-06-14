package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton

@Composable
fun Login(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val loginButtonEnabled = viewModel.loginEnabled.collectAsState().value
    val showSpinner = viewModel.showSpinner.collectAsState().value
    val errorTextId = viewModel.errorTextId.collectAsState().value
    LoginInner(
        viewModel.phone,
        viewModel.password,
        viewModel::onLoginClicked,
        loginButtonEnabled,
        showSpinner,
        errorTextId,
        navController,
    )
}

@Composable
fun LoginInner(
    phone: TextFieldState,
    password: TextFieldState,
    loginFunc: ()->Unit,
    loginEnabled: Boolean,
    showSpinner: Boolean,
    errorTextId: Int,
    navController: NavController,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(320.dp).testTag("Logo"))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        phone,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        lineLimits = TextFieldLineLimits.SingleLine,

                        placeholder = { Text(stringResource(R.string.username)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SecureTextField(
                        password,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        placeholder = { Text(stringResource(R.string.password)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LoadingButton(
                        { loginFunc() },
                        stringResource(R.string.login_button),
                        loginEnabled,
                        showSpinner
                    )
                    LoadingButton(
                        onClick = { navController.navigate("createAccount") },
                        text = stringResource(R.string.create_account),
                        enabled = true,
                        showSpinner = false
                    )
                    if (errorTextId != 0) {
                        Text(text = stringResource(errorTextId), color = Color.Red)
                    }

                }
            }
        }

    }
}

@Preview
@Composable
fun LoginEnabled() {
    LoginInner(
        TextFieldState(""),
        TextFieldState(""),
        {},
        loginEnabled = true,
        showSpinner = false,
        errorTextId = 0,
        NavController(LocalContext.current)
    )
}

@Preview
@Composable
fun LoginSpinner() {
    LoginInner(
        TextFieldState(""),
        TextFieldState(""),
        {},
        loginEnabled = false,
        showSpinner = true,
        errorTextId = 0,
        NavController(LocalContext.current)
    )
}


@Preview
@Composable
fun LoginErrorText() {
    LoginInner(
        TextFieldState(""),
        TextFieldState(""),
        {},
        loginEnabled = true,
        showSpinner = false,
        errorTextId = R.string.network_error,
        NavController(LocalContext.current)

    )
}
