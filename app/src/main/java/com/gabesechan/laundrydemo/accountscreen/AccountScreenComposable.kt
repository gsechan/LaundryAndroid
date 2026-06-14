package com.gabesechan.laundrydemo.accountscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.AddressDisplay
import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User

@Composable
fun AccountScreen(navController: NavController, viewModel: AccountScreenViewModel = hiltViewModel()) {
    val user = viewModel.user.collectAsState().value
    AccountScreenInner(
        user,
        viewModel::onLogoutClicked,
        viewModel::deleteAddress,
        { address -> navController.navigate("editAddress/${address.id}") },
    )

}

@Composable
fun AccountScreenInner(
    user: User,
    logoutClicked: ()->Unit,
    onDeleteAddress: (Address)->Unit,
    onEditAddress: (Address)->Unit = {},
) {
    Column(
        Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.name,
            )
            Text(text = stringResource(R.string.phone_label, user.phone))
            Text(text = stringResource(R.string.email_label, user.email ?: ""))
        }

        Column {

            if (user.addresses.isNotEmpty()) {
                Text(stringResource(R.string.addresses))
                Spacer(modifier = Modifier.height(8.dp))
            }
            user.addresses.forEach { address ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AddressDisplay(address, modifier = Modifier.padding(16.dp))
                        Row(modifier = Modifier.align(Alignment.TopEnd)) {
                            IconButton(
                                onClick = { onEditAddress(address) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = stringResource(R.string.edit_address)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteAddress(address) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = stringResource(R.string.delete_address)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button(onClick = logoutClicked, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {
            Text(stringResource(R.string.logout))
        }

    }
}

