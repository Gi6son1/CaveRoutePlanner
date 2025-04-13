package com.majorproject.caverouteplanner.ui.screens

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.MarkupImageAndGraphOverlay
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.customcomposables.ActionCheckDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton
import com.majorproject.caverouteplanner.ui.components.markuplayouts.AltitudesLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.DistanceAndCompassCalibrationLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.EntrancesAndJunctionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.PathConnectionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.WaterAndHardTraverseLayout
import com.majorproject.caverouteplanner.ui.util.calculateCoordinatePixels
import com.majorproject.caverouteplanner.ui.util.getNearestLine
import com.majorproject.caverouteplanner.ui.util.getNearestNode

val OffsetSaver = Saver<Offset, Pair<Float, Float>>(
    save = { offset -> Pair(offset.x, offset.y) },
    restore = { pair -> Offset(pair.first, pair.second) }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyMarkupScreen(
    returnToMenu: () -> Unit = {},
) {
    val context = LocalContext.current.applicationContext
    var markupStage by rememberSaveable { mutableIntStateOf(0) }
    val titleList = remember {
        listOf(
            "Compass/Distance calibration",
            "Entrances/Junctions",
            "Connect Paths",
            "Water/Hard Traverse",
            "Altitude"
        )
    }

    var currentlySelectedMarkupOption by rememberSaveable { mutableIntStateOf(0) }

    val surveyImage =
        BitmapFactory.decodeStream(context.assets.open("llygadlchwr.jpg")).asImageBitmap()

    LaunchedEffect(markupStage) {
        currentlySelectedMarkupOption = 0
    }


    var openHomeButtonDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var nodesList by rememberSaveable { mutableStateOf(listOf<SurveyNode>()) }
    var pathsList by rememberSaveable { mutableStateOf(listOf<SurveyPath>()) }

    var northMarker by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var centreMarker by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var distanceMarker1 by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var distanceMarker2 by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }

    BackGroundScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Stage ${markupStage + 1}: ${titleList[markupStage]}") },
            )
        }
    ) { innerPadding ->


        MarkupImageAndGraphOverlay(
            surveyImage = surveyImage,
            nodes = nodesList,
            paths = pathsList,
            modifier = Modifier.padding(innerPadding),
            markupStage = markupStage,
            northMarker = northMarker,
            centreMarker = centreMarker,
            distanceMarker1 = distanceMarker1,
            distanceMarker2 = distanceMarker2,
            longPressPosition = { longPressPosition ->

            },
            onTapPosition = { tapPosition ->
                when (markupStage) {
                    0 -> {
                        when (currentlySelectedMarkupOption) {
                            1 -> northMarker = tapPosition
                            2 -> centreMarker = tapPosition
                            3 -> distanceMarker1 = tapPosition
                            4 -> distanceMarker2 = tapPosition
                        }
                    }
                    1 -> {
                        if (currentlySelectedMarkupOption == 1 || currentlySelectedMarkupOption == 2) {
                            val adjustedPixelsCoordinates = calculateCoordinatePixels(
                                tapPosition,
                                IntSize(
                                    width = surveyImage.width,
                                    height = surveyImage.height
                                )
                            )
                            val newList = nodesList + SurveyNode(
                                x = adjustedPixelsCoordinates.x.toInt(),
                                y = adjustedPixelsCoordinates.y.toInt(),
                                surveyId = -1,
                                isEntrance = currentlySelectedMarkupOption == 1,
                                isJunction = currentlySelectedMarkupOption == 2
                            )
                            nodesList = newList
                        }
                        else if (currentlySelectedMarkupOption == 3) {
                            val foundNode = getNearestNode(tapPosition, nodesList, surveyImage.width, surveyImage.height, 0.001f)
                            if (foundNode != null) {
                                val newList = nodesList.toMutableList()
                                newList.remove(foundNode)
                                nodesList = newList
                            }
                        }
                    }
                    2 -> {

                    }
                    3 -> {
                        val foundPath = getNearestLine(tapPosition, nodesList, pathsList, surveyImage.width, surveyImage.height, 0.25f)
                        if (foundPath != null) {
                            val newList = pathsList.toMutableList()
                            when (currentlySelectedMarkupOption) {
                                1 -> newList[newList.indexOf(foundPath)].hasWater = true
                                2 -> newList[newList.indexOf(foundPath)].isHardTraverse = true
                                3 -> {
                                    newList[newList.indexOf(foundPath)].hasWater = false
                                    newList[newList.indexOf(foundPath)].isHardTraverse = false
                                }
                            }
                            pathsList = newList
                        }
                    }
                    4 -> {
                        val foundPath = getNearestLine(tapPosition, nodesList, pathsList, surveyImage.width, surveyImage.height, 0.25f)
                        if (foundPath != null) {
                            val newList = pathsList.toMutableList()
                            newList[newList.indexOf(foundPath)].altitude = currentlySelectedMarkupOption-5
                            pathsList = newList
                        }
                    }
                }
            }
        )

        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val (stageButtons, homeButton, customLayout, saveButton) = createRefs()

            CustomIconButton(
                onClick = { openHomeButtonDialog = true },
                modifier = Modifier.constrainAs(homeButton) {
                    top.linkTo(parent.top, margin = 20.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                },
                iconVector = Icons.Outlined.Home,
                contentDescription = "Home"
            )

            CustomIconButton(
                onClick = {},
                modifier = Modifier.constrainAs(saveButton) {
                    top.linkTo(homeButton.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                },
                iconVector = Icons.Outlined.Save,
                contentDescription = "Save",
            )

            Row(modifier = Modifier.constrainAs(stageButtons) {
                bottom.linkTo(parent.bottom, 20.dp)
                start.linkTo(parent.start, 10.dp)
                end.linkTo(parent.end, 10.dp)
                width = Dimension.fillToConstraints
            }) {
                if (markupStage > 0) {
                    CustomTextButton(
                        onClick = {
                            markupStage--
                        },
                        text = titleList[markupStage - 1],
                        flipped = true,
                        modifier = Modifier.weight(1f),
                        iconVector = Icons.AutoMirrored.Outlined.ArrowBack
                    )
                }
                if (markupStage < titleList.size - 1) {
                    CustomTextButton(
                        onClick = {
                            markupStage++
                        },
                        text = titleList[markupStage + 1],
                        modifier = Modifier.weight(1f),
                        iconVector = Icons.AutoMirrored.Outlined.ArrowForward
                    )
                }
            }

            when (markupStage) {
                0 -> DistanceAndCompassCalibrationLayout(
                    modifier = Modifier
                        .constrainAs(customLayout) {
                            bottom.linkTo(stageButtons.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 10.dp)
                            top.linkTo(parent.top, 20.dp)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    updateCurrentlySelected = { currentlySelectedMarkupOption = it },
                    currentlySelectedSetting = currentlySelectedMarkupOption
                )
                1 -> EntrancesAndJunctionsLayout(
                    modifier = Modifier
                        .constrainAs(customLayout) {
                            bottom.linkTo(stageButtons.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 10.dp)
                            top.linkTo(parent.top, 20.dp)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    updateCurrentlySelected = { currentlySelectedMarkupOption = it },
                    currentlySelectedSetting = currentlySelectedMarkupOption
                )

                2 -> PathConnectionsLayout(
                    modifier = Modifier
                        .constrainAs(customLayout) {
                            bottom.linkTo(stageButtons.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 10.dp)
                            top.linkTo(parent.top, 20.dp)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    updateCurrentlySelected = { currentlySelectedMarkupOption = it },
                    currentlySelectedSetting = currentlySelectedMarkupOption
                )

                3 -> WaterAndHardTraverseLayout(
                    modifier = Modifier
                        .constrainAs(customLayout) {
                            bottom.linkTo(stageButtons.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 10.dp)
                            top.linkTo(parent.top, 20.dp)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    updateCurrentlySelected = { currentlySelectedMarkupOption = it },
                    currentlySelectedSetting = currentlySelectedMarkupOption
                )

                4 -> AltitudesLayout(
                    modifier = Modifier
                        .constrainAs(customLayout) {
                            bottom.linkTo(stageButtons.top, margin = 20.dp)
                            end.linkTo(parent.end, margin = 10.dp)
                            top.linkTo(parent.top, 20.dp)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    updateCurrentlySelected = { currentlySelectedMarkupOption = it },
                    currentlySelectedSetting = currentlySelectedMarkupOption
                )
            }
        }

        ActionCheckDialog(
            dialogIsOpen = openHomeButtonDialog,
            dialogOpen = { openHomeButtonDialog = it },
            confirmAction = { returnToMenu() },
            message = "Are you sure you'd like to go back to the main menu? You will lose your current route setup if you do."
        )

    }
}
