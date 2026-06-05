package com.gabesechan.laundrydemo.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(
            text = "Home screen",
        )
        Text(
            text = "This is a place to display messages, deals, etc.",
        )

    }
}
