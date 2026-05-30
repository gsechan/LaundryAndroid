package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(onClick: ()->Unit, text: String, enabled:Boolean, showSpinner: Boolean) {

    Button(
        onClick = { onClick() },
        modifier = Modifier.height(ButtonDefaults.MinHeight).fillMaxWidth().testTag("LoadingButtonRoot"),
        // Disable button while loading to prevent multiple clicks
        enabled = enabled
    ) {
        if (showSpinner) {
            // Indeterminate indicator that runs while task is active
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp).testTag("LoadingProgress"),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}