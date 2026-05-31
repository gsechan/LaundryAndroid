package com.gabesechan.laundrydemo.accountscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.user.Address
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
        Spacer(modifier = Modifier.height(16.dp))
        if(user.addresses.isNotEmpty()) {
            Text(stringResource(R.string.addresses))
        }
        user.addresses.forEach {
            AddressDisplay(it)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = logoutClicked) {
            Text("Logout")
        }

    }
}


@Composable
fun AddressDisplay(address: Address) {
    Text(address.street1)
    if(address.street2 != null) {
        Text(address.street2)
    }
    Text(stringResource(R.string.address_format, address.city, address.state, address.postcode))

}
