package com.majorproject.caverouteplanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromInternalStorage
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.util.calculateAngle
import com.majorproject.caverouteplanner.ui.util.calculateDistance
import com.majorproject.caverouteplanner.ui.util.calculateFractionalOffset
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * This composable is used to display the image of the survey with the graph overlain on top
 *
 * @param survey The survey to display
 * @param modifier The modifier to apply to the layout
 * @param currentRoute The current route
 * @param longPressPosition The method used to update the long press position
 * @param onTap The method used to handle a tap
 * @param pinpointSourceNode The node to add a source pinpoint to
 * @param pinpointDestinationNode The node to add a destination pinpoint to
 * @param compassEnabled Whether the compass should be enabled
 * @param compassReading The current compass reading
 */
@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier,
    currentRoute: Route? = null,
    longPressPosition: (Offset) -> Unit = {},
    onTap: () -> Unit = {},
    pinpointSourceNode: SurveyNode? = null,
    pinpointDestinationNode: SurveyNode? = null,
    compassEnabled: Boolean,
    compassReading: Double?
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    var focusedRotation by remember { mutableFloatStateOf(0f) }
    var focusedOffset by remember { mutableStateOf(Offset.Zero) }
    var focusedCentroid by remember { mutableStateOf<Pair<Int, Int>?>(null) }

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

    LaunchedEffect(currentRoute?.currentStage) { //this is called everytime the current stage changes
        if (currentRoute != null && currentRoute.currentStage != -1) {
            performFocusedTransformation( //retrieve the angle, size and centroid of the current route
                currentRoute,
                survey.nodes,
            ) { angle, distance, centroid -> //change the zoom, rotation and offset of the image to focus on the current route
                focusedCentroid = centroid


                val referenceDimension =
                    maxOf(survey.properties.width, survey.properties.height).toFloat()
                val fractionalZoom = distance / referenceDimension

                zoom = (1f / fractionalZoom) * 0.8f

                val rotate = -angle + 270
                rotation = rotate
                focusedRotation = rotation

                val boxCenterX = boxSize.width / 2f
                val boxCenterY = boxSize.height / 2f

                if (centroid == null) return@performFocusedTransformation

                val centroidX =
                    (centroid.first.toFloat() / survey.properties.width) * boxSize.width
                val centroidY =
                    (centroid.second.toFloat() / survey.properties.height) * boxSize.height

                var targetOffsetX = boxCenterX - centroidX
                var targetOffsetY = boxCenterY - centroidY

                val angleRad = Math.toRadians(rotation.toDouble()).toFloat()

                val rotatedOffsetX =
                    (targetOffsetX * cos(angleRad) - targetOffsetY * sin(angleRad))
                val rotatedOffsetY =
                    (targetOffsetX * sin(angleRad) + targetOffsetY * cos(angleRad))

                val focusedTranslation = Offset(rotatedOffsetX * zoom, rotatedOffsetY * zoom)

                offset = focusedTranslation
                focusedOffset = offset
            }
        } else if (currentRoute != null) {
            offset = Offset.Zero
            rotation = 0f
            zoom = 1f
        }
    }

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
                    if (!currentlyTransformGesturing) { //only allow long press if not currently using transform gestures
                        detectTapGestures(
                            onLongPress = { longPressLoc ->
                                val fractionalTapPosition =
                                    calculateFractionalOffset(longPressLoc, boxSize)
                                longPressPosition(fractionalTapPosition)
                            },
                            onTap = {
                                onTap()
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

            LaunchedEffect(survey) { //called each time a survey changes
                imageBitmap = getBitmapFromInternalStorage(
                    survey.properties.imageReference
                )
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "survey",
                    modifier = Modifier,
                    contentScale = ContentScale.Fit
                )
            }

            GraphOverlay(
                nodes = survey.nodes,
                modifier = Modifier.matchParentSize(),
                surveySize = IntSize(
                    width = survey.properties.width,
                    height = survey.properties.height
                ),
                currentRoute = currentRoute,
                destinationNode = pinpointDestinationNode,
                pinpointNode = pinpointSourceNode,
                currentRotation = rotation,
                currentZoom = zoom,
                paths = survey.paths,
                compassRotation = if (compassEnabled) (compassReading?.plus(survey.properties.northAngle.toDouble())) else null
            )
        }
    }
}

/**
 * Function for returning the values for a focused view of a given route
 *
 * @param currentRoute The current route
 * @param nodes The nodes of the survey
 * @param calculatedTransformation The method used to update the transformation values
 */
private fun performFocusedTransformation(
    currentRoute: Route,
    nodes: List<SurveyNode>,
    calculatedTransformation: (Float, Float, Pair<Int, Int>?) -> Unit
) {
    /**
     * Calculates the centroid of the current route, finds the combined centroid of all paths on the route
     *
     * @param currentStage The current stage of the route
     * @return The centroid of the route
     *
     */
    fun calculateCentroid(currentStage: List<SurveyPath>): Pair<Int, Int>? {
        var cumulativeCentroidX = 0f
        var cumulativeCentroidY = 0f


        for (path in currentStage) {
            val firstNode = nodes.find { it.getNodeId() == path.getPathEnds().first } ?: return null
            val secondNode =
                nodes.find { it.getNodeId() == path.getPathEnds().second } ?: return null
            cumulativeCentroidX += (firstNode.x + secondNode.x) / 2
            cumulativeCentroidY += (firstNode.y + secondNode.y) / 2
        }

        return Pair(
            cumulativeCentroidX.toInt() / currentStage.size,
            cumulativeCentroidY.toInt() / currentStage.size
        )

    }

    val currentStage = currentRoute.getCurrentStage()
    val startNode = nodes.find { it.getNodeId() == currentRoute.getCurrentStartingNode() }
    val endNode = nodes.find { it.getNodeId() == currentRoute.getCurrentEndingNode() }

    if (startNode == null || endNode == null) return

    val finalAngle = calculateAngle(Pair(startNode.x.toFloat(), startNode.y.toFloat()), Pair(endNode.x.toFloat(), endNode.y.toFloat()))

    val centroid = calculateCentroid(currentStage)

    val finalDistance =
        calculateDistance(Pair(startNode.x, startNode.y), Pair(endNode.x, endNode.y))

    calculatedTransformation(finalAngle.toFloat(), finalDistance, centroid)
}

/**
 * This composable is used to display the graph canvas overlay on top of the survey image
 *
 * @param modifier The modifier to apply to the layout
 * @param nodes The nodes of the survey
 * @param paths The paths of the survey
 * @param surveySize The size of the survey
 * @param currentRoute The current route
 * @param pinpointNode The node to pinpoint
 * @param destinationNode The node to pinpoint to
 * @param currentRotation The current rotation of the image
 * @param currentZoom The current zoom of the image
 * @param compassRotation The current compass rotation
 */
@Composable
fun GraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    surveySize: IntSize,
    currentRoute: Route? = null,
    pinpointNode: SurveyNode? = null,
    destinationNode: SurveyNode? = null,
    currentRotation: Float,
    currentZoom: Float,
    compassRotation: Double?
) {
    val pinpointIcon = ImageBitmap.imageResource(id = R.drawable.location_icon)
    val destinationIcon = ImageBitmap.imageResource(id = R.drawable.destination_icon)
    val currentDirectionIcon = ImageBitmap.imageResource(id = R.drawable.direction_icon)
    val arrowHead = ImageBitmap.imageResource(id = R.drawable.arrow_head)

    val adjustedZoom = if (currentZoom == 0f) 0.000001f else currentZoom //avoid division by zero

    Canvas(modifier = modifier) {
        if (currentRoute != null) { //if there is a current route, draw the route with paths
            currentRoute.routeList.forEachIndexed { index, pathList ->
                if (currentRoute.currentStage != -1 && index == currentRoute.currentStage) { //draw the current path in green
                    currentRoute.getCurrentStage().forEach { path ->
                        val startNode = nodes.find { it.getNodeId() == path.getPathEnds().first }!!
                        val endNode = nodes.find { it.getNodeId() == path.getPathEnds().second }!!

                        drawLine(
                            color = Color(0xFF00B126),
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
                } else { //draw the rest of the paths in blue or grey
                    pathList.forEach { path ->
                        val startNode = nodes.find { it.getNodeId() == path.getPathEnds().first }!!
                        val endNode = nodes.find { it.getNodeId() == path.getPathEnds().second }!!

                        drawLine(
                            color = if (path.hasWater) Color(0xFF007ADD) else Color(0xFFA3A3A3),
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
        } else { //otherwise, draw all paths in the survey, but make them slightly transparent
            paths.forEach { path ->
                val startNode = nodes.find { it.getNodeId() == path.getPathEnds().first }!!
                val endNode = nodes.find { it.getNodeId() == path.getPathEnds().second }!!

                drawLine(
                    color = if (path.hasWater) Color(0xFF007ADD).copy(alpha = 0.5f) else Color(
                        0xFFA3A3A3
                    ).copy(alpha = 0.5f),
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

        if (pinpointNode != null && (currentRoute == null || currentRoute.routeStarted == false)) { //draw the pinpoint (source) icon if the route hasn't started
            val iconOffsetX = pinpointIcon.width
            val iconOffsetY = pinpointIcon.height.toFloat() * 2

            val pinPointCoodsX = (pinpointNode.x - iconOffsetX).toFloat()
            val pinPointCoodsY = (pinpointNode.y - iconOffsetY).toFloat()

            withTransform(
                {

                    translate(
                        left = 2f,
                        top = 2f
                    )
                    scale(
                        scaleX = 0.75f / adjustedZoom,
                        scaleY = 0.75f / adjustedZoom,
                        pivot = Offset(
                            (pinpointNode.x / surveySize.width.toFloat()) * size.width,
                            (pinpointNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )
                    rotate(
                        degrees = -currentRotation,
                        pivot = Offset(
                            (pinpointNode.x / surveySize.width.toFloat()) * size.width,
                            (pinpointNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )
                }
            ) {
                drawImage(
                    image = pinpointIcon,
                    topLeft = Offset(
                        (pinPointCoodsX / surveySize.width.toFloat()) * size.width,
                        (pinPointCoodsY / surveySize.height.toFloat()) * size.height
                    )
                )
            }


        }



        if (currentRoute?.routeStarted == true) { //if the route has started, draw the direction icon on the start node of the current path and the arrow at the end of the path
            val currentStartNode =
                nodes.find { it.getNodeId() == currentRoute.getCurrentStartingNode() }
            if (currentStartNode == null) {
                return@Canvas
            }
            val currentPathEndNode =
                nodes.find {
                    it.getNodeId() == currentRoute.getCurrentStage().first()
                        .next(currentStartNode.getNodeId())
                }

            val currentStartAngle = calculateAngle(
                Pair(currentStartNode.x.toFloat(), currentStartNode.y.toFloat()),
                Pair(currentPathEndNode!!.x.toFloat(), currentPathEndNode.y.toFloat())
            ).toFloat()

            val adjustedStartAngle = currentStartAngle + 90

            val iconOffsetX = currentDirectionIcon.width.toFloat()
            val iconOffsetY = currentDirectionIcon.height.toFloat()

            val pinPointCoodsX = (currentStartNode.x - iconOffsetX).toFloat()
            val pinPointCoodsY = (currentStartNode.y - iconOffsetY).toFloat()

            withTransform(
                {
                    translate(
                        left = -4f / adjustedZoom,
                        top = -4f / adjustedZoom
                    )
                    scale(
                        scaleX = 0.15f,
                        scaleY = 0.15f,
                        pivot = Offset(
                            (currentStartNode.x / surveySize.width.toFloat()) * size.width,
                            (currentStartNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )
                    if (compassRotation != null) {
                        rotate(
                            degrees = compassRotation.toFloat(),
                            pivot = Offset(
                                (currentStartNode.x / surveySize.width.toFloat()) * size.width,
                                (currentStartNode.y / surveySize.height.toFloat()) * size.height
                            )
                        )
                    } else {
                        rotate(
                            degrees = adjustedStartAngle,
                            pivot = Offset(
                                (currentStartNode.x / surveySize.width.toFloat()) * size.width,
                                (currentStartNode.y / surveySize.height.toFloat()) * size.height
                            )
                        )
                    }
                }
            ) {
                drawImage(
                    image = currentDirectionIcon,
                    topLeft = Offset(
                        (pinPointCoodsX / surveySize.width.toFloat()) * size.width,
                        (pinPointCoodsY / surveySize.height.toFloat()) * size.height
                    )
                )
            }

            val currentEndNode =
                nodes.find { it.getNodeId() == currentRoute.getCurrentEndingNode() }
            if (currentEndNode == null) return@Canvas
            val currentEndPathStartNode =
                nodes.find {
                    it.getNodeId() == currentRoute.getCurrentStage().last()
                        .next(currentEndNode.getNodeId())
                }

            val currentEndAngle = calculateAngle(
                Pair(currentEndPathStartNode!!.x.toFloat(), currentEndPathStartNode.y.toFloat()),
                Pair(currentEndNode.x.toFloat(), currentEndNode.y.toFloat())
            ).toFloat()

            val adjustedEndAngle = currentEndAngle + 90

            val arrowIconOffsetX = arrowHead.width.toFloat()
            val arrowIconOffsetY = arrowHead.height.toFloat() * 0.5

            val arrowPointCoodsX = (currentEndNode.x - arrowIconOffsetX).toFloat()
            val arrowPointCoodsY = (currentEndNode.y - arrowIconOffsetY).toFloat()

            withTransform(
                {
                    translate(
                        left = -2f / adjustedZoom,
                        top = -2f / adjustedZoom
                    )
                    scale(
                        scaleX = 0.125f,
                        scaleY = 0.125f,
                        pivot = Offset(
                            (currentEndNode.x / surveySize.width.toFloat()) * size.width,
                            (currentEndNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )

                    rotate(
                        degrees = adjustedEndAngle,
                        pivot = Offset(
                            (currentEndNode.x / surveySize.width.toFloat()) * size.width,
                            (currentEndNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )


                }
            ) {
                drawImage(
                    image = arrowHead,
                    topLeft = Offset(
                        (arrowPointCoodsX / surveySize.width.toFloat()) * size.width,
                        (arrowPointCoodsY / surveySize.height.toFloat()) * size.height
                    )
                )
            }

        }


        if (destinationNode != null) { //if there is a destination node, draw the destination icon at the end of the route
            val iconOffsetX = 0
            val iconOffsetY = destinationIcon.height.toFloat() * 2

            val pinPointCoodsX = (destinationNode.x - iconOffsetX).toFloat()
            val pinPointCoodsY = (destinationNode.y - iconOffsetY).toFloat()

            withTransform(
                {
                    scale(
                        scaleX = 0.75f / adjustedZoom,
                        scaleY = 0.75f / adjustedZoom,
                        pivot = Offset(
                            (destinationNode.x / surveySize.width.toFloat()) * size.width,
                            (destinationNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )
                    rotate(
                        degrees = -currentRotation,
                        pivot = Offset(
                            (destinationNode.x / surveySize.width.toFloat()) * size.width,
                            (destinationNode.y / surveySize.height.toFloat()) * size.height
                        )
                    )
                }
            ) {
                drawImage(
                    image = destinationIcon,
                    topLeft = Offset(
                        (pinPointCoodsX / surveySize.width.toFloat()) * size.width,
                        (pinPointCoodsY / surveySize.height.toFloat()) * size.height
                    )
                )
            }
        }
    }
}
