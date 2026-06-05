package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { if (value > min) onValueChange(value - 1) },
            enabled = value > min,
            shape = CircleShape
        ) {
            Text("-")
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(
            onClick = { if (value < max) onValueChange(value + 1) },
            enabled = value < max,
            shape = CircleShape
        ) {
            Text("+")
        }
    }
}