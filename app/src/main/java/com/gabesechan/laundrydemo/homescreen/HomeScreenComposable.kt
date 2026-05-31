package com.gabesechan.laundrydemo.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen() {
    Column {
        Text(
            text = "Home screen",
        )
        Text(
            text = "This is a place to display messages, deals, etc.",
        )

    }
}
