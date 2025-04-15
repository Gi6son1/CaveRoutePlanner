package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.majorproject.caverouteplanner.ui.components.enums.Difficulty
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme


/**
 * val name: String,
 *     val length: Float,
 *     val description: String,
 *     val difficulty: String,
 *     val location: String,
 */

@Composable
fun SaveSurveyDialog(
    dialogIsOpen: Boolean, //checks if the dialog should be open
    dialogOpen: (Boolean) -> Unit = {}, //method used for closing the dialog,
    saveSurvey: (String, Float, String, Difficulty, String) -> Unit
) {
    var name: String by rememberSaveable { mutableStateOf("") }
    var length: Float by rememberSaveable { mutableStateOf(0f) }
    var description: String by rememberSaveable { mutableStateOf("") }
    var difficulty: Difficulty by rememberSaveable { mutableStateOf(Difficulty.NONE) }
    var location: String by rememberSaveable { mutableStateOf("") }

    if (dialogIsOpen) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = true),
            onDismissRequest = {}, //sets it so that user cannot dismiss dialog by tapping outside
        ) {



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
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = length.toString(),
                        onValueChange = { length = it.toFloat() },
                        label = { Text(text = "Length (m)") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(text = "Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Start),
                        text = "Cave Difficulty"
                    )
                    SingleChoiceSegmentedButtonRow {
                        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
                        SingleChoiceSegmentedButtonRow {
                            val difficultyList = Difficulty.all()
                            difficultyList.forEachIndexed { index, label ->
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
                                        text = difficultyList[index].toString().lowercase().replaceFirstChar{ it.uppercase() },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(text = "Location") },
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
            saveSurvey = { name, length, description, difficulty, location -> }
        )
    }
}