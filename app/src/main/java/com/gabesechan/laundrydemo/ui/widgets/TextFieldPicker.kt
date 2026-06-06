package com.gabesechan.laundrydemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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
    label: String?,
    value: String,
    dialogContent: @Composable ((T)->Unit)->Unit,
    onSelected: (T) -> Unit,
    @DrawableRes icon: Int? = null,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            state = TextFieldState(initialText = value),
            label = {
                if(label != null)
                    Text(label)
            },
            labelPosition = TextFieldLabelPosition.Above(),
            lineLimits = TextFieldLineLimits.SingleLine,
            readOnly = true,
            trailingIcon = {
                if(icon != null) {
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

            //Not a date picker, but it has a well styled dialog that mostly works like we want
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
