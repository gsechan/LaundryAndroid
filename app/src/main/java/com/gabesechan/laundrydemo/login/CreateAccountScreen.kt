package com.gabesechan.laundrydemo.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip

@Composable
fun CreateAccountScreen(viewModel: CreateAccountViewModel = hiltViewModel()) {
    val createEnabled by viewModel.createEnabled.collectAsState()
    val createSpinner by viewModel.createRunning.collectAsState()

    CreateAccountScreenInner(
        viewModel.name,
        viewModel.password1,
        viewModel.password2,
        viewModel.phone,
        viewModel.email,
        viewModel::createAccountClicked,
        createEnabled,
        createSpinner
    )
}

@Composable
fun CreateAccountScreenInner(
    nameState: TextFieldState,
    password1: TextFieldState,
    password2: TextFieldState,
    phoneState: TextFieldState,
    emailState: TextFieldState,
    onCreateClicked: ()->Unit,
    createEnabled: Boolean,
    createSpinner: Boolean
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){

            TextField(
                nameState,
                placeholder = { Text(stringResource(R.string.enter_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                password1,
                placeholder = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                password2,
                placeholder = { Text(stringResource(R.string.repeat_password)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                phoneState,
                placeholder = { Text(stringResource(R.string.enter_phone)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                emailState,
                placeholder = { Text(stringResource(R.string.enter_email)) },
                modifier = Modifier.fillMaxWidth()
            )
            //Add all textfields
            LoadingButton(onCreateClicked, stringResource(R.string.create_account), createEnabled,createSpinner)
        }
    }


}