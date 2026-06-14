package com.gabesechan.laundrydemo.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.ui.widgets.LoadingButton
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun AddAddressScreen(navController: NavController, viewModel: AddAddressViewModel = hiltViewModel()) {
    val addEnabled by viewModel.createEnabled.collectAsState()
    val addSpinner by viewModel.addRunning.collectAsState()

    CreateAccountScreenInner(
        viewModel.street1,
        viewModel.street2,
        viewModel.city,
        viewModel.state,
        viewModel.country,
        viewModel.postcode,
        viewModel::addAccountClicked,
        addEnabled,
        addSpinner,
        viewModel.navEvent,
        navController,
        viewModel.networkError,
    )
}

@Composable
fun CreateAccountScreenInner(
    street1: TextFieldState,
    street2: TextFieldState,
    city: TextFieldState,
    state: TextFieldState,
    country: TextFieldState,
    postcode: TextFieldState,
    onAddClicked: ()->Unit,
    createEnabled: Boolean,
    createSpinner: Boolean,
    navEvent: SharedFlow<Unit>,
    navController: NavController,
    networkError: Boolean,
) {
    LaunchedEffect(Unit) {
        navEvent.collect { _ ->
            navController.popBackStack()
        }
    }

    if(networkError) {
        Column(Modifier.fillMaxHeight().padding(12.dp)) {
            Text(stringResource(R.string.network_error))
        }
        return
    }

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
                street1,
                placeholder = { Text(stringResource(R.string.enter_street1)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                street2,
                placeholder = { Text(stringResource(R.string.enter_street2)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                country,
                placeholder = { Text(stringResource(R.string.enter_country)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                city,
                placeholder = { Text(stringResource(R.string.enter_city)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                state,
                placeholder = { Text(stringResource(R.string.enter_state)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                postcode,
                placeholder = { Text(stringResource(R.string.enter_postcode)) },
                modifier = Modifier.fillMaxWidth()
            )
            //Add all textfields
            LoadingButton(onAddClicked, stringResource(R.string.add_new_address), createEnabled,createSpinner)
        }
    }


}