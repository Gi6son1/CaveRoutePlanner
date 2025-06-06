package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chillibits.composenumberpicker.HorizontalNumberPicker
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

/**
 * This composable is used to display a dialog for travel condition choices
 * The user can choose if they prefer no water, no hard traverse or high altitude paths, as well as the number of cavers caving
 *
 * @param dialogIsOpen Whether the dialog should be open
 * @param dialogOpen The method used to close the dialog
 * @param currentConditions The current conditions of the journey
 * @param updatedConditions The method used to update the conditions
 * @param currentNumberOfTravellers The current number of cavers caving
 */
@Composable
fun TravelConditionsDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog
    currentConditions: Triple<Boolean, Boolean, Boolean>,
    updatedConditions: (Boolean, Boolean, Boolean, Int) -> Unit = { _, _, _, _ -> },
    currentNumberOfTravellers: Int,
) {
    if (dialogIsOpen) {
        Dialog(
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
        ) {

            BackHandler { dialogOpen(false) }

            var noWater by rememberSaveable { mutableStateOf(currentConditions.first) }

            var noHardTraverse by rememberSaveable { mutableStateOf(currentConditions.second) }

            var highAltitude by rememberSaveable { mutableStateOf(currentConditions.third) }

            var numberOfTravellers by rememberSaveable { mutableIntStateOf(currentNumberOfTravellers) }

            Card {
                Column {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(R.string.travel_conditions),
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
                            .height(50.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                        ) {
                            Text(text = stringResource(R.string.no_water_preferred))
                            Text(text = stringResource(R.string.paths_with_water_will_be_avoided), fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = noWater,
                            onCheckedChange = { noWater = it },
                            modifier = Modifier
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
                            .height(50.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                        ) {
                            Text(text = stringResource(R.string.no_hard_traverse_preferred))
                            Text(
                                text = stringResource(R.string.paths_with_hard_traverses_will_be_avoided),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = noHardTraverse,
                            onCheckedChange = { noHardTraverse = it },
                            modifier = Modifier
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
                            .height(50.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                        ) {
                            Text(text = stringResource(R.string.high_altitude_preferred))
                            Text(text = stringResource(R.string.lower_altitude_paths_will_be_avoided), fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = highAltitude,
                            onCheckedChange = { highAltitude = it },
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.how_many_cavers),
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight()
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        HorizontalNumberPicker(
                            min = 1,
                            max = 10,
                            default = numberOfTravellers,
                            onValueChange = { numberOfTravellers = it },
                            modifier = Modifier
                        )
                    }

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
                                updatedConditions(
                                    noWater,
                                    noHardTraverse,
                                    highAltitude,
                                    numberOfTravellers
                                )
                            },
                            text = stringResource(R.string.confirm_choices),
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
fun TravelConditionsDialogPreview() {
    CaveRoutePlannerTheme {
        TravelConditionsDialog(
            dialogIsOpen = true,
            currentConditions = Triple(true, true, true),
            currentNumberOfTravellers = 1,
        )
    }
}