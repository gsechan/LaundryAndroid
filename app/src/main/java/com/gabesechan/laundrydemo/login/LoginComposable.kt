package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton

@Composable
fun Login(viewModel: LoginViewModel = hiltViewModel()) {
    val loginButtonEnabled = viewModel.loginButtonEnabled.collectAsState().value
    val showSpinner = viewModel.showSpinner.collectAsState().value
    val errorTextId = viewModel.errorTextId.collectAsState().value
    LoginInner(
        viewModel::onLoginClicked,
        loginButtonEnabled,
        showSpinner,
        errorTextId
    )
}

@Composable
fun LoginInner(
    loginFunc: (CharSequence, CharSequence)->Unit,
    loginEnabled: Boolean,
    showSpinner: Boolean,
    errorTextId: Int
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize()
        ) {
            Text(
                text = "Login screen",
            )
            val usernameState = rememberTextFieldState("")
            val passwordState = rememberTextFieldState("")
            TextField(usernameState, placeholder = {Text(stringResource(R.string.username))})
            SecureTextField(passwordState,
                placeholder = {Text(stringResource(R.string.password))},
            )
            LoadingButton(
                {loginFunc(usernameState.text, passwordState.text)},
                stringResource(R.string.login_button),
                loginEnabled && usernameState.text.isNotEmpty() && passwordState.text.isNotEmpty(),
                showSpinner
            )
            if(errorTextId != 0) {
                Text(text= stringResource(errorTextId),  color = Color.Red)
            }
        }

    }
}

