package com.gabesechan.laundrydemo.login

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun CreateAccountScreen(viewModel: CreateAccountViewModel = hiltViewModel()) {

    if(viewModel.neworkError) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.network_error))
        }
    }
    else {
        val createEnabled by viewModel.createEnabled.collectAsState()
        val createSpinner by viewModel.createRunning.collectAsState()
        val pasword1SupportText by viewModel.passWordSuppotingText.collectAsState()
        val pasword2SupportText by viewModel.passWordSuppotingText2.collectAsState()
        val phoneSupportText by viewModel.phoneSupportingText.collectAsState()
        val emailSupportText by viewModel.emailSupportingText.collectAsState()

        CreateAccountScreenInner(
            viewModel.name,
            viewModel.password1,
            pasword1SupportText,
            viewModel.password2,
            pasword2SupportText,
            viewModel.phone,
            phoneSupportText,
            viewModel.email,
            emailSupportText,
            viewModel::createAccountClicked,
            createEnabled,
            createSpinner,
        )
    }
}

object PasswordOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        replace(0, length, "•".repeat(length))
    }
}

@Composable
fun CreateAccountScreenInner(
    nameState: TextFieldState,
    password1: TextFieldState,
    @StringRes password1SupportText: Int,
    password2: TextFieldState,
    @StringRes password2SupportText: Int,
    phoneState: TextFieldState,
    @StringRes phoneSupportText: Int,
    emailState: TextFieldState,
    @StringRes emailSupportText: Int,
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
                outputTransformation  = PasswordOutputTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = { Text(stringResource(R.string.password)) },
                supportingText = { Text(stringResource(password1SupportText), color= Color.Red)},
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                password2,
                outputTransformation  = PasswordOutputTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = { Text(stringResource(R.string.repeat_password)) },
                supportingText = { Text(stringResource(password2SupportText), color= Color.Red)},
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                phoneState,
                placeholder = { Text(stringResource(R.string.enter_phone)) },
                supportingText = { Text(stringResource(phoneSupportText), color= Color.Red)},
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                emailState,
                placeholder = { Text(stringResource(R.string.enter_email)) },
                supportingText = { Text(stringResource(emailSupportText), color= Color.Red)},
                modifier = Modifier.fillMaxWidth()
            )
            //Add all textfields
            LoadingButton(onCreateClicked, stringResource(R.string.create_account), createEnabled,createSpinner)
        }
    }


}