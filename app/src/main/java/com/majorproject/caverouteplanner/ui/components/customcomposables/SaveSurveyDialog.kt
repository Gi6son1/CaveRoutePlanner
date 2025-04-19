package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.components.enums.Difficulty
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

/**
 * This composable is used to display a dialog for saving a survey
 * The user can enter the name, length, description, difficulty and location of the cave
 *
 * @param dialogIsOpen Whether the dialog should be open
 * @param dialogOpen The method used to close the dialog
 * @param saveSurvey The method used to save the survey
 */
@Composable
fun SaveSurveyDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog,
    saveSurvey: (String, Int, String, Difficulty, String) -> Unit
) {
    var name: String by rememberSaveable { mutableStateOf("") }
    var length: Int by rememberSaveable { mutableIntStateOf(0) }
    var description: String by rememberSaveable { mutableStateOf("") }
    var difficulty: Difficulty by rememberSaveable { mutableStateOf(Difficulty.NONE) }
    var location: String by rememberSaveable { mutableStateOf("") }

    val nameCharLimit = 20
    val descriptionCharLimit = 100
    val intCharLimit = 10
    val locationCharLimit = 20

    if (dialogIsOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = true),
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
        ) {

            BackHandler { /*nothing happens when back is pressed*/ }

            Card {
                Column(modifier = Modifier.padding(10.dp)) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Save to New Cave",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= nameCharLimit) name = it },
                        label = { Text(text = stringResource(R.string.name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = length.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                length = -1
                            } else if (it.all { char -> char.isDigit() } && it.length <= intCharLimit) {
                                    length = it.toInt()
                            }
                        },
                        label = { Text(text = stringResource(R.string.length_m)) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= descriptionCharLimit) description = it },
                        label = { Text(text = stringResource(R.string.description)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start),
                        text = stringResource(R.string.cave_difficulty)
                    )
                    SingleChoiceSegmentedButtonRow {
                        var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
                        SingleChoiceSegmentedButtonRow {
                            val difficultyList = Difficulty.all()
                            difficultyList.forEachIndexed { index, _ ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = difficultyList.size
                                    ),
                                    onClick = {
                                        selectedIndex = index
                                        difficulty = difficultyList[index]
                                    },
                                    selected = selectedIndex == index
                                )
                                {
                                    Text(
                                        text = difficultyList[index].toString().lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { if (it.length <= locationCharLimit) location = it },
                        label = { Text(text = stringResource(R.string.location)) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(40.dp))


                    Row {
                        CustomTextButton(
                            onClick = { dialogOpen(false) },
                            text = "Cancel",
                            iconVector = Icons.Outlined.Close,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        CustomTextButton(
                            onClick = {
                                saveSurvey(name, length, description, difficulty, location)
                                dialogOpen(false)
                            },
                            text = "Save",
                            iconVector = Icons.Outlined.Save,
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
fun SaveSurveyDialogPreview() {
    CaveRoutePlannerTheme {
        SaveSurveyDialog(
            dialogIsOpen = true,
            dialogOpen = {},
            saveSurvey = { _, _, _, _, _ -> }
        )
    }
}