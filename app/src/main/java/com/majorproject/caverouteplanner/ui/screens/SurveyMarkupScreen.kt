package com.majorproject.caverouteplanner.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository
import com.majorproject.caverouteplanner.datasource.util.copyImageToInternalStorageFromTemp
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromTempInternalStorage
import com.majorproject.caverouteplanner.model.viewmodel.CaveRoutePlannerViewModel
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.MarkupImageAndGraphOverlay
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.customcomposables.ActionCheckDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.SaveSurveyDialog
import com.majorproject.caverouteplanner.ui.components.enums.Difficulty
import com.majorproject.caverouteplanner.ui.components.markuplayouts.AltitudesLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.DistanceAndCompassCalibrationLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.EntrancesAndJunctionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.PathConnectionsLayout
import com.majorproject.caverouteplanner.ui.components.markuplayouts.WaterAndHardTraverseLayout
import com.majorproject.caverouteplanner.ui.util.calculateAngle
import com.majorproject.caverouteplanner.ui.util.calculateCoordinatePixels
import com.majorproject.caverouteplanner.ui.util.calculateDistance
import com.majorproject.caverouteplanner.ui.util.calculateMetersFromFractionalOffsets
import com.majorproject.caverouteplanner.ui.util.calculatePixelsPerMeter
import com.majorproject.caverouteplanner.ui.util.getNearestLine
import com.majorproject.caverouteplanner.ui.util.getNearestNode
import kotlinx.coroutines.launch
import kotlin.math.round

val OffsetSaver = Saver<Offset, Pair<Float, Float>>(
    save = { offset -> Pair(offset.x, offset.y) },
    restore = { pair -> Offset(pair.first, pair.second) }
)

@Composable
fun SurveyMarkupScreenTopLevel(
    returnToMenu: () -> Unit = {},
    viewModel: CaveRoutePlannerViewModel
) {
    val context = LocalContext.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(getBitmapFromTempInternalStorage(context)) }

    if (imageBitmap != null) {
        SurveyMarkupScreen(
            returnToMenu = returnToMenu,
            saveCaveAndSurvey = { caveProperties, surveyProperties, nodes, paths ->
                viewModel.saveNewCave(caveProperties, surveyProperties, nodes, paths)
                returnToMenu()
            },
            imageBitmap = imageBitmap!!
        )
    } else {
        returnToMenu()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyMarkupScreen(
    returnToMenu: () -> Unit = {},
    imageBitmap: ImageBitmap,
    saveCaveAndSurvey: (CaveProperties, SurveyProperties, List<SurveyNode>, List<SurveyPath>) -> Unit = {_, _, _, _ ->}
) {
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

    LaunchedEffect(markupStage) {
        currentlySelectedMarkupOption = 0
    }

    val context = LocalContext.current


    var openHomeButtonDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var openSaveDialog by rememberSaveable { mutableStateOf(false) }

    var nodeIdCount by rememberSaveable { mutableIntStateOf(0) }

    var nodesList by rememberSaveable { mutableStateOf(listOf<SurveyNode>()) }
    var pathsList by rememberSaveable { mutableStateOf(listOf<SurveyPath>()) }

    var northMarker by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var centreMarker by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var distanceMarker1: Offset by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var distanceMarker2: Offset by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    var pixelsPerMeter by rememberSaveable { mutableFloatStateOf(1f) }

    var currentlySelectedSurveyNode by rememberSaveable { mutableStateOf<SurveyNode?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var savedSurveyReference by rememberSaveable { mutableStateOf<String?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var length by rememberSaveable { mutableIntStateOf(0) }
    var description by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableStateOf(Difficulty.NONE) }
    var location by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(savedSurveyReference) {
        if (savedSurveyReference != null) {
            fun convertToMeters(pathList: List<SurveyPath>, pixelsPerMeter: Float): List<SurveyPath> {
                var convertedPathList = pathList
                for (path in convertedPathList) {
                    path.distance = (path.distance / pixelsPerMeter)
                }
                return convertedPathList
            }


            var caveProperties: CaveProperties = CaveProperties(
                name = name,
                length = length,
                description = description,
                difficulty = difficulty.displayName,
                location = location,
                surveyId = -1
            )

            var formattedPathList = convertToMeters(pathsList, pixelsPerMeter)

            var northAngle: Float = if (northMarker == Offset.Zero && centreMarker == Offset.Zero) {
                -90f
            } else {
                calculateAngle(
                    Pair(round(northMarker.x).toInt(), round(northMarker.y).toInt()),
                    Pair(round(centreMarker.x).toInt(), round(centreMarker.x).toInt())
                ).toFloat() - 90f
            }

            var surveyProperties: SurveyProperties = SurveyProperties(
                width = imageBitmap.width,
                height = imageBitmap.height,
                pixelsPerMeter = pixelsPerMeter,
                imageReference = savedSurveyReference!!,
                northAngle = northAngle
            )

            saveCaveAndSurvey(
                caveProperties,
                surveyProperties,
                nodesList,
                formattedPathList
            )
        }
    }


    BackGroundScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Stage ${markupStage + 1}: ${titleList[markupStage]}") },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        BackHandler { openHomeButtonDialog = true }

        MarkupImageAndGraphOverlay(
            nodes = nodesList,
            paths = pathsList,
            modifier = Modifier.padding(innerPadding),
            markupStage = markupStage,
            northMarker = northMarker,
            imageBitmap = imageBitmap,
            centreMarker = centreMarker,
            distanceMarker1 = distanceMarker1,
            distanceMarker2 = distanceMarker2,
            currentlySelectedSurveyNode = currentlySelectedSurveyNode,
            longPressPosition = { longPressPosition ->
                if (markupStage == 2 && currentlySelectedMarkupOption == 1) {
                    val foundNode = getNearestNode(
                        longPressPosition,
                        nodesList,
                        imageBitmap.width,
                        imageBitmap.height,
                        0.01f
                    )
                    if (foundNode != null && (foundNode.isEntrance || foundNode.isJunction)) {
                        currentlySelectedSurveyNode = foundNode
                    }
                }
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
                                    width = imageBitmap.width,
                                    height = imageBitmap.height
                                )
                            )
                            val newList = nodesList + SurveyNode(
                                x = adjustedPixelsCoordinates.x.toInt(),
                                y = adjustedPixelsCoordinates.y.toInt(),
                                surveyId = -1,
                                isEntrance = currentlySelectedMarkupOption == 1,
                                isJunction = currentlySelectedMarkupOption == 2,
                                id = nodeIdCount++
                            )
                            nodesList = newList
                        } else if (currentlySelectedMarkupOption == 3) {
                            val foundNode = getNearestNode(
                                tapPosition,
                                nodesList,
                                imageBitmap.width,
                                imageBitmap.height,
                                0.005f
                            )
                            if (foundNode != null) {
                                val newList = nodesList.toMutableList()
                                newList.remove(foundNode)
                                nodesList = newList

                                val newPathList = pathsList.toMutableList()
                                newPathList.removeIf { it.getPathEnds().first == foundNode.id || it.getPathEnds().second == foundNode.id }
                                pathsList = newPathList
                            }
                        }
                    }

                    2 -> {
                        if (currentlySelectedMarkupOption == 1 && currentlySelectedSurveyNode != null) {
                            var nextSurveyNode = getNearestNode(
                                tapPosition,
                                nodesList,
                                imageBitmap.width,
                                imageBitmap.height,
                                0.005f
                            )
                            if (nextSurveyNode == null || (!nextSurveyNode.isEntrance && !nextSurveyNode.isJunction)) {
                                val adjustedPixelsCoordinates = calculateCoordinatePixels(
                                    tapPosition,
                                    IntSize(
                                        width = imageBitmap.width,
                                        height = imageBitmap.height
                                    )
                                )
                                nextSurveyNode = SurveyNode(
                                    x = adjustedPixelsCoordinates.x.toInt(),
                                    y = adjustedPixelsCoordinates.y.toInt(),
                                    surveyId = -1,
                                    id = nodeIdCount++,
                                )
                                val newNodeList = nodesList.toMutableList()
                                newNodeList.add(nextSurveyNode)
                                nodesList = newNodeList
                            }

                            val newPathList = pathsList.toMutableList()
                            newPathList.add(
                                SurveyPath(
                                    ends = Pair(
                                        currentlySelectedSurveyNode!!.id,
                                        nextSurveyNode.id
                                    ),
                                    distance = calculateDistance(
                                        Pair(
                                            currentlySelectedSurveyNode!!.x,
                                            currentlySelectedSurveyNode!!.y
                                        ), Pair(nextSurveyNode.x, nextSurveyNode.y)
                                    ),
                                    surveyId = -1,
                                )
                            )
                            pathsList = newPathList
                            currentlySelectedSurveyNode = nextSurveyNode

                        } else if (currentlySelectedMarkupOption == 2) {
                            val foundEdge = getNearestLine(
                                tapPosition,
                                nodesList,
                                pathsList,
                                imageBitmap.width,
                                imageBitmap.height,
                                0.5f
                            )
                            if (foundEdge != null) {
                                val newPathList = pathsList.toMutableList()
                                val newNodeList = nodesList.toMutableList()

                                cleanupGraphFromEdge(
                                    foundEdge,
                                    newNodeList,
                                    newPathList,
                                )

                                if (!newNodeList.contains(currentlySelectedSurveyNode)) {
                                    currentlySelectedSurveyNode = null
                                }

                                pathsList = newPathList
                                nodesList = newNodeList
                            }
                        }

                    }

                    3 -> {
                        val foundPath = getNearestLine(
                            tapPosition,
                            nodesList,
                            pathsList,
                            imageBitmap.width,
                            imageBitmap.height,
                            0.5f
                        )
                        if (foundPath != null) {
                            val newList = pathsList.toMutableList()
                            when (currentlySelectedMarkupOption) {
                                1 -> newList[newList.indexOf(foundPath)] =
                                    foundPath.copy(hasWater = true)

                                2 -> newList[newList.indexOf(foundPath)] =
                                    foundPath.copy(isHardTraverse = true)

                                3 -> {
                                    newList[newList.indexOf(foundPath)] =
                                        foundPath.copy(hasWater = false, isHardTraverse = false)
                                }
                            }
                            pathsList = newList
                        }
                    }

                    4 -> {
                        val foundPath = getNearestLine(
                            tapPosition,
                            nodesList,
                            pathsList,
                            imageBitmap.width,
                            imageBitmap.height,
                            0.5f
                        )
                        if (foundPath != null) {
                            val newList = pathsList.toMutableList()
                            newList[newList.indexOf(foundPath)] = foundPath.copy(
                                altitude =
                                currentlySelectedMarkupOption
                            )
                            pathsList = newList
                            Log.d("Paths", pathsList.toString())
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
                onClick = {
                    openSaveDialog = true
                },
                modifier = Modifier.constrainAs(saveButton) {
                    top.linkTo(homeButton.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                },
                iconVector = Icons.Outlined.Save,
                contentDescription = "Save",
            )

            Row(
                modifier = Modifier.constrainAs(stageButtons) {
                    bottom.linkTo(parent.bottom, 20.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    width = Dimension.fillToConstraints
                },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0..titleList.size - 1) {
                    CustomSmallTextButton(
                        onClick = { markupStage = i },
                        text = (i + 1).toString(),
                        currentlySelected = i == markupStage,
                        square = true
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
                    currentlySelectedSetting = currentlySelectedMarkupOption,
                    metersBetweenPoints = (calculateMetersFromFractionalOffsets(
                        distanceMarker1,
                        distanceMarker2,
                        IntSize(
                            width = imageBitmap.width,
                            height = imageBitmap.height
                        ),
                        pixelsPerMeter
                    )),
                    updatePixelsPerMeter = { numberOfMeters ->
                        pixelsPerMeter = calculatePixelsPerMeter(
                            distanceMarker1,
                            distanceMarker2,
                            IntSize(
                                width = imageBitmap.width,
                                height = imageBitmap.height
                            ),
                            numberOfMeters
                        )
                    }
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
            message = "Are you sure you'd like to go back to the main menu? You will lose your current markup if you do."
        )

        SaveSurveyDialog(
            dialogIsOpen = openSaveDialog,
            dialogOpen = { openSaveDialog = it },
            saveSurvey = { enterName, enterLength, enterDescription, enterDifficulty, enterLocation ->

                val errorMessage = validateInputs(
                    enterName,
                    enterLength,
                    enterDifficulty,
                    nodesList,
                    pathsList,
                    distanceMarker1,
                    distanceMarker2
                )

                name = enterName
                length = enterLength
                description = enterDescription
                difficulty = enterDifficulty
                location = enterLocation

                if (errorMessage != null){
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                } else {
                    savedSurveyReference = copyImageToInternalStorageFromTemp(context = context, imageName = "$name.jpg")
                }
            }
        )
    }
}


fun cleanupGraphFromEdge(
    foundEdge: SurveyPath,
    nodes: MutableList<SurveyNode>,
    paths: MutableList<SurveyPath>
) {
    val newPathList = paths.toMutableList()
    val newNodeList = nodes.toMutableList()

    newPathList.remove(foundEdge)

    // Cleanup from the first node of the edge
    cleanupFromNode(foundEdge.getPathEnds().first, newNodeList, newPathList)

    // Cleanup from the second node of the edge
    cleanupFromNode(foundEdge.getPathEnds().second, newNodeList, newPathList)

    paths.clear()
    paths.addAll(newPathList)
    nodes.clear()
    nodes.addAll(newNodeList)
}

private fun cleanupFromNode(
    startNodeId: Int,
    nodes: MutableList<SurveyNode>,
    paths: MutableList<SurveyPath>
) {
    var currentNode: SurveyNode? = nodes.find { it.getNodeId() == startNodeId }
    while (currentNode != null && !currentNode.isEntrance && !currentNode.isJunction) {
        nodes.remove(currentNode)

        val connectingPath =
            paths.find { it.getPathEnds().first == currentNode.getNodeId() || it.getPathEnds().second == currentNode.getNodeId() }

        if (connectingPath != null) {
            paths.remove(connectingPath)

            val nextNodeId = connectingPath.next(currentNode.getNodeId())
            currentNode = nodes.find { it.getNodeId() == nextNodeId }
        } else {
            currentNode = null
        }
    }
}

private fun validateInputs(
    name: String,
    length: Int,
    difficulty: Difficulty,
    nodesList: List<SurveyNode>,
    pathsList: List<SurveyPath>,
    distanceMarker1: Offset,
    distanceMarker2: Offset,
) : String?{
    fun validateNodeConnections(): Boolean {
        for (node in nodesList) {
            if (pathsList.none { it.getPathEnds().first == node.getNodeId() || it.getPathEnds().second == node.getNodeId() }) {
                return false
            }
        }
        return true
    }


    var errorMessage: String? = null
    if (name.isBlank()){
        errorMessage = "Name cannot be blank"
    } else if (length < 0){
        errorMessage = "Length cannot be negative or empty"
    } else if (difficulty == Difficulty.NONE){
        errorMessage = "Please choose a difficulty for this cave"
    } else if (nodesList.isEmpty()){
        errorMessage = "Please add at least one node"
    } else if (pathsList.isEmpty()){
        errorMessage = "Please add at least one path"
    } else if (distanceMarker1 == Offset.Zero || distanceMarker2 == Offset.Zero){
        errorMessage = "Please calibrate the distance markers"
    } else if (!validateNodeConnections()){
        errorMessage = "All nodes must be have at least one path connected to it"
    }
    return errorMessage
}
