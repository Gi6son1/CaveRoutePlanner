package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ActionCheckDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog
    actionDialogMessage: String,
    performMainAction: (Boolean) -> Unit = {}, //hoists state
) {
    if (dialogIsOpen) {
        Dialog(
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
            properties = DialogProperties(usePlatformDefaultWidth = false) //allows a nonstandard width for the dialog
        ) {
            BackHandler {
                dialogOpen(false) //closes dialog when back button is pressed
            }

            Column( //column to hold the dialog
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .height(200.dp)
            ) {
            }
        }
    }
}

@Preview
@Composable
fun ActionCheckDialogPreview(){
    ActionCheckDialog(
        dialogIsOpen = true,
        actionDialogMessage = "Are you sure you want to do this?",
    )
}
