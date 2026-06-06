package com.gabesechan.laundrydemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.gabesechan.laundrydemo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Displays a text field.  Clicking on it allows you to select a date via a modal popup.  Selecting
 * a date closes the modal
 *
 * placeholder-  the label text for the field
 * selectableDates-  a filter allowing or denying dates from being selected
 * onDateSelected-  a callback used to inform when a date was selected.
 */
@Composable
fun <T> TextFieldPicker(
    label: String,
    value: String,
    dialogContent: @Composable ((T)->Unit)->Unit,
    onSelected: (T) -> Unit,
    @DrawableRes icon: Int = 0,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            state = TextFieldState(initialText = value),
            label = { Text(label)},
            labelPosition = TextFieldLabelPosition.Above(),
            lineLimits = TextFieldLineLimits.SingleLine,
            readOnly = true,
            trailingIcon = {
                if(icon != 0) {
                    IconButton(onClick = { showDialog = !showDialog }) {
                        Icon(
                            painterResource(icon),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Select date"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(null) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDialog = true
                        }
                    }
                }

        )

        if (showDialog) {
            Popup(
                onDismissRequest = { showDialog = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {

                    DatePickerDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                        },
                        dismissButton = {
                        },
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            dialogContent { selected ->
                                showDialog = false
                                onSelected(selected)
                            }
                        }
                    }
                }
            }
        }
    }
}
