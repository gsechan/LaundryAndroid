package com.gabesechan.laundrydemo.accountscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.AddressDisplay
import com.gabesechan.laundrydemo.user.Address
import com.gabesechan.laundrydemo.user.User

@Composable
fun AccountScreen(navController: NavController, viewModel: AccountScreenViewModel = hiltViewModel()) {
    val user = viewModel.user.collectAsState().value
    AccountScreenInner(user, viewModel::onLogoutClicked)

}

@Composable
fun AccountScreenInner(user: User, logoutClicked: ()->Unit) {
    Column(
        Modifier.fillMaxHeight().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.name,
            )
            Text(text = stringResource(R.string.phone_label, user.phone ?: ""))
            Text(text = stringResource(R.string.email_label, user.email ?: ""))
        }

        Column {

            if (user.addresses.isNotEmpty()) {
                Text(stringResource(R.string.addresses))
                Spacer(modifier = Modifier.height(8.dp))
            }
            user.addresses.forEach {
                AddressDisplay(it, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button(onClick = logoutClicked, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {
            Text("Logout")
        }

    }
}

