package com.gabesechan.laundrydemo.accountscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.login.LoginViewModel
import com.gabesechan.laundrydemo.user.User

@Composable
fun AccountScreen(viewModel: AccountScreenViewModel = hiltViewModel()) {
    val user = viewModel.user.collectAsState().value
    AccountScreenInner(user, viewModel::onLogoutClicked)

}

@Composable
fun AccountScreenInner(user: User, logoutClicked: ()->Unit) {
    Column {
        Text(
            text = user.name,
        )
        Text(text = stringResource(R.string.phone_label, user.phone ?:""))
        Text(text = stringResource(R.string.email_label, user.email ?:""))
        Button(onClick = logoutClicked) {
            Text("Logout")
        }

    }

}