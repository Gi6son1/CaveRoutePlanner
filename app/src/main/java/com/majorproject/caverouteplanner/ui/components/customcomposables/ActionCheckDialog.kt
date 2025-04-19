package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

/**
 * This composable is used to display a dialog that asks the user if they are sure they want to do something
 *
 * @param dialogIsOpen Whether the dialog should be open
 * @param dialogOpen A method used for closing the dialog
 * @param message The message to display in the dialog
 * @param confirmAction A method used for confirming the action if the user selects confirm
 */
@Composable
fun ActionCheckDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog
    message: String,
    confirmAction: () -> Unit = {},
) {
    if (dialogIsOpen) {
        Dialog(
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
        ) {

            BackHandler { dialogOpen(false) } //if back button is pressed, close the dialog

            Card {
                Column {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(R.string.are_you_sure),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(horizontal = 10.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.padding(10.dp)) {
                        CustomTextButton(
                            onClick = { dialogOpen(false) },
                            text = stringResource(R.string.cancel),
                            iconVector = Icons.Outlined.Close,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        CustomTextButton(
                            onClick = {
                                dialogOpen(false)
                                confirmAction()
                            },
                            text = stringResource(R.string.i_am_sure),
                            iconVector = Icons.Outlined.Check,
                            modifier = Modifier.weight(1f)
                        )
                    }

                }
            }

        }
    }
}

@Preview
@Composable
fun ActionCheckDialogPreview() {
    CaveRoutePlannerTheme {
        ActionCheckDialog(
            dialogIsOpen = true,
            message = "Are you sure you want to do this? bla bla bla bla bla bla bla bla fdf skladjf lskdf ",
        )
    }

}
