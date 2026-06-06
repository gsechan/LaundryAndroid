package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.user.Address

@Composable
fun AddressPicker(
    addresses: List<Address>,
    selectedAddress: Address,
    onSelection: (Address)->Unit,
) {

    TextFieldPicker(
        stringResource(R.string.select_address),
        selectedAddress.street1,
        { callback ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.select_address))
                addresses.forEachIndexed { index, address ->
                    AddressDisplay(address, Modifier.clickable() {
                        callback(address)
                    })
                    if(index != addresses.size -1) {
                        Spacer(modifier = Modifier.fillMaxWidth().background(Color.Black).height(2.dp))
                    }
                }
            }
        },
        onSelection
    )
}

@Composable
fun AddressDisplay(address: Address, modifier: Modifier= Modifier) {
    Column(modifier = modifier) {
        Text(address.street1)
        if (address.street2 != null) {
            Text(address.street2)
        }
        Text(stringResource(R.string.address_format, address.city, address.state, address.postcode))
    }
}
