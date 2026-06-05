package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.user.Address

@Composable
fun AddressPicker(
    addresses: List<Address>,
    selectedIndex: Int,
    onSelection: (Int)->Unit,
) {

    Column(Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        addresses.forEachIndexed { index, address ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (index == selectedIndex),
                        onClick = { onSelection(index) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected =  (index == selectedIndex),
                    onClick = null // null recommended for accessibility with screen readers
                )
                AddressDisplay(address)
            }
        }
    }
}

@Composable
fun AddressDisplay(address: Address) {
    Column {
        Text(address.street1)
        if (address.street2 != null) {
            Text(address.street2)
        }
        Text(stringResource(R.string.address_format, address.city, address.state, address.postcode))
    }
}
