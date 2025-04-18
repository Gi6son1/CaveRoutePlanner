package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DoubleArrow
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

@Composable
fun CaveExitDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog
    highAltitudeExit: () -> Unit = {},
    normalExit: () -> Unit = {},
) {
    if (dialogIsOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
        ) {

            BackHandler { dialogOpen(false) }

            Card(modifier = Modifier.width(370.dp)) {
                Column {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(R.string.route_to_nearest_exit),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(80.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                                .weight(6f)
                        ) {
                            Text(text = stringResource(R.string.standard))
                            Text(
                                text = stringResource(R.string.takes_the_best_path_given_current_travel_conditions),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.weight(0.5f))
                        CustomTextButton(
                            onClick = {
                                dialogOpen(false)
                                normalExit()
                            },
                            text = "This One",
                            iconVector = Icons.Outlined.DoubleArrow,
                            modifier = Modifier
                                .weight(4f)
                                .fillMaxHeight()
                                .wrapContentHeight()
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(80.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                                .weight(6f)
                        ) {
                            Text(text = stringResource(R.string.emergency_flood_exit))
                            Text(
                                text = stringResource(R.string.takes_the_highest_altitude_paths_ignoring_other_travel_conditions),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                        CustomTextButton(
                            onClick = {
                                dialogOpen(false)
                                highAltitudeExit()
                            },
                            text = "This One",
                            iconVector = Icons.Outlined.DoubleArrow,
                            modifier = Modifier
                                .weight(4f)
                                .fillMaxHeight()
                                .wrapContentHeight()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    CustomTextButton(
                        onClick = { dialogOpen(false) },
                        text = "Cancel",
                        iconVector = Icons.Outlined.Close,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .padding(10.dp)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun CaveExitDialogPreview() {
    CaveRoutePlannerTheme {
        CaveExitDialog(
            dialogIsOpen = true,
        )
    }
}