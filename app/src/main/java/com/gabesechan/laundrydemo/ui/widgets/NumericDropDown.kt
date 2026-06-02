package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumericDropdownMenu(
    label: String,
    minValue: Int,
    maxValue: Int,
    defaultValue: Int,
    onNumberSelected: (Int) -> Unit
) {
    // Manages whether the dropdown popup is visible
    var isExpanded by remember { mutableStateOf(false) }

    // Tracks the currently chosen number (null indicates no initial selection)
    var selectedNumber by remember { mutableStateOf<Int>(defaultValue) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        ) {
            OutlinedTextField(
                // Displays the integer converted to text, or an empty string if null
                value = selectedNumber?.toString() ?: "",
                onValueChange = {},
                readOnly = true, // Disables standard keyboard input
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Correctly targets and positions the dropdown layout
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                for( number in minValue..maxValue) {
                    DropdownMenuItem(
                        text = { Text(text = number.toString()) },
                        onClick = {
                            selectedNumber = number
                            isExpanded = false
                            onNumberSelected(number) // Sends the selection back to parent
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun numericDropDown() {
    NumericDropdownMenu("Label", 0,5, 0, {})
}