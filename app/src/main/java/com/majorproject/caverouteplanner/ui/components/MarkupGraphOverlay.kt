package com.majorproject.caverouteplanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.ui.util.calculateFractionalOffset
import kotlin.math.max

/**
 * Composable to hold the iamge and graph overlay for the markup screen, it is a reduced version of the image and graph overlay composable
 *
 * @param paths list of paths to display
 * @param nodes list of nodes to display
 * @param modifier modifier
 * @param longPressPosition function to call when long press is detected
 * @param onTapPosition function to call when tap is detected
 * @param markupStage current markup stage that decided what should be displayed on the graph overlay
 * @param northMarker north marker to display
 * @param centreMarker centre marker to display
 * @param distanceMarker1 distance marker 1 to display
 * @param distanceMarker2 distance marker 2 to display
 * @param imageBitmap image bitmap
 * @param currentlySelectedSurveyNode currently selected survey node
 */
@Composable
fun MarkupImageAndGraphOverlay(
    paths: List<SurveyPath>,
    nodes: List<SurveyNode>,
    modifier: Modifier = Modifier,
    longPressPosition: (Offset) -> Unit,
    onTapPosition: (Offset) -> Unit,
    markupStage: Int,
    northMarker: Offset,
    centreMarker: Offset,
    distanceMarker1: Offset,
    distanceMarker2: Offset,
    imageBitmap: ImageBitmap,
    currentlySelectedSurveyNode: SurveyNode?
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current
    val currentConfiguration = LocalConfiguration.current

    //these are saved so that they're not recalculated every recomposition, they only change when the screen dimensions change i.e. phone rotate
    val screenWidth = remember(currentConfiguration) {
        with(density) { currentConfiguration.screenWidthDp.dp.toPx() }
    }
    val screenHeight = remember(currentConfiguration) {
        with(density) { currentConfiguration.screenHeightDp.dp.toPx() }
    }

    var currentlyTransformGesturing by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures(
                    panZoomLock = true,
                    onGesture = { _, pan, newZoom, newRotate ->
                        val adjustedScale =
                            (zoom * newZoom).coerceIn(1f, 20f)

                        val boxScaledWidth = boxSize.width * adjustedScale
                        val boxScaledHeight = boxSize.height * adjustedScale

                        //keep these above 0, otherwise randomly they could go below and cause error
                        val maxOffsetX = max(0f, (boxScaledWidth - screenWidth))
                        val maxOffsetY = max(0f, (boxScaledHeight - screenHeight))

                        // If survey size is bigger than the screen, user can pan survey
                        val adjustedOffset =
                            if (boxScaledWidth > screenWidth || boxScaledHeight > screenHeight) {
                                val newOffset = offset + pan
                                Offset(
                                    x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                    y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                                )
                            } else {
                                Offset.Zero //otherwise no panning offset occurs
                            }


                        zoom = adjustedScale
                        rotation += newRotate
                        offset = adjustedOffset

                    },
                )
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressedPointers = event.changes.count { it.pressed }

                        currentlyTransformGesturing = pressedPointers >= 2
                    }
                }
            }
    ) {
        val (overlay) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(overlay) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .onGloballyPositioned { coordinates ->
                    boxSize = coordinates.size
                }
                .pointerInput(currentlyTransformGesturing) {
                    if (!currentlyTransformGesturing) {
                        detectTapGestures(
                            onLongPress = { longPressLoc ->
                                val fractionalTapPosition =
                                    calculateFractionalOffset(longPressLoc, boxSize)
                                longPressPosition(fractionalTapPosition)
                            },
                            onTap = { tapLoc ->
                                val fractionalTapPosition =
                                    calculateFractionalOffset(tapLoc, boxSize)
                                onTapPosition(fractionalTapPosition)
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {


            Image(
                bitmap = imageBitmap,
                contentDescription = "survey",
                modifier = Modifier,
                contentScale = ContentScale.Fit
            )

            MarkupGraphOverlay(
                nodes = nodes,
                modifier = Modifier.matchParentSize(),
                surveySize = IntSize(
                    width = imageBitmap.width,
                    height = imageBitmap.height
                ),
                paths = paths,
                markupStage = markupStage,
                northMarker = northMarker,
                centreMarker = centreMarker,
                distanceMarker1 = distanceMarker1,
                distanceMarker2 = distanceMarker2,
                currentlySelectedSurveyNode = currentlySelectedSurveyNode
            )
        }

    }
}

/**
 * Composable to hold the graph overlay for the markup screen - it is a modified version of the graph overlay composable
 * @param modifier modifier
 * @param nodes list of nodes to display
 * @param paths list of paths to display
 * @param surveySize size of the survey
 * @param markupStage current markup stage that decided what should be displayed on the graph overlay
 * @param northMarker north marker to display
 * @param centreMarker centre marker to display
 * @param distanceMarker1 distance marker 1 to display
 * @param distanceMarker2 distance marker 2 to display
 * @param currentlySelectedSurveyNode currently selected survey node
 */
@Composable
fun MarkupGraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    surveySize: IntSize,
    markupStage: Int,
    northMarker: Offset,
    centreMarker: Offset,
    distanceMarker1: Offset,
    distanceMarker2: Offset,
    currentlySelectedSurveyNode: SurveyNode?
) {
    val altitudeColours = remember { //colours for the different altitudes
        listOf(
            Color(0xFF001433),
            Color(0xFF003366),
            Color(0xFF005C99),
            Color(0xFF0086A8),
            Color(0xFF00997A),
            Color(0xFF228B22),
            Color(0xFF6B8E23),
            Color(0xFFB8860B),
            Color(0xFFCD6600),
            Color(0xFFB22222),
            Color(0xFF800000)
        )
    }

    Canvas(modifier = modifier) {
        if (markupStage == 0) { //if the stage is 0, then only show the calibration markers
            drawCircle(
                color = Color(0xFF05166b),
                radius = 2f,
                center = Offset(
                    northMarker.x * size.width,
                    northMarker.y * size.height
                )
            )
            drawCircle(
                color = Color(0xFF730606),
                radius = 2f,
                center = Offset(
                    centreMarker.x * size.width,
                    centreMarker.y * size.height
                )
            )
            drawCircle(
                color = Color(0xFF228B22),
                radius = 2f,
                center = Offset(
                    distanceMarker1.x * size.width,
                    distanceMarker1.y * size.height
                )
            )
            drawCircle(
                color = Color(0xFF228B22),
                radius = 2f,
                center = Offset(
                    distanceMarker2.x * size.width,
                    distanceMarker2.y * size.height
                )
            )
        }

        if (markupStage == 1 || markupStage == 2) { //if the stage is 1 or 2, then show the nodes
            nodes.forEach { node ->
                drawCircle(
                    color = when {
                        node.isEntrance -> Color(0xFF05166b)
                        node.isJunction -> Color(0xFF730606)
                        markupStage == 1 -> Color.Transparent //if the stage is 1, then show the non-junction/entrance nodes as transparent
                        else -> Color.DarkGray
                    },
                    radius = if (currentlySelectedSurveyNode == node && markupStage == 2) 5f else if (node.isEntrance || node.isJunction) 3f else 2f,
                    center = Offset(
                        (node.x / surveySize.width.toFloat()) * size.width,
                        (node.y / surveySize.height.toFloat()) * size.height
                    )
                )
            }
        }
        if (markupStage == 2 || markupStage == 3 || markupStage == 4) { //if the stage is 2 or 3 or 4, then show the paths
            paths.forEach { path ->
                val startNode = nodes.find { it.getNodeId() == path.getPathEnds().first }!!
                val endNode = nodes.find { it.getNodeId() == path.getPathEnds().second }!!

                drawLine(
                    color = when {
                        markupStage == 4 -> altitudeColours[path.altitude + 5] //if the stage is 4, then show the paths in the correct altitude colour
                        markupStage == 2 -> Color.DarkGray //if the stage is 2, then show the paths in gray
                        path.isHardTraverse && path.hasWater -> Color(0xFF36056b)
                        path.isHardTraverse -> Color(0xFF730606)
                        path.hasWater -> Color(0xFF05166b)
                        else -> Color.DarkGray
                    },

                    start = Offset(
                        (startNode.x / surveySize.width.toFloat()) * size.width,
                        (startNode.y / surveySize.height.toFloat()) * size.height
                    ),
                    end = Offset(
                        (endNode.x / surveySize.width.toFloat()) * size.width,
                        (endNode.y / surveySize.height.toFloat()) * size.height
                    ),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
