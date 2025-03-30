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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier,
    currentRoute: Route? = null,
    longPressPosition: (Offset) -> Unit = {},
    pinpointSourceNode: Int? = null,
    pinpointDestinationNode: Int? = null
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

    LaunchedEffect(currentRoute?.currentStage) {
        if (currentRoute != null && currentRoute.currentStage != -1) {
            performFocusedTransformation(
                currentRoute,
                survey.pathNodes,
            ) { angle, distance, centroid ->

                val referenceDimension = maxOf(survey.size.first, survey.size.second).toFloat()
                val fractionalZoom = distance / referenceDimension

                zoom = (1f / fractionalZoom) * 0.8f

                val rotate = -angle + 270
                rotation = rotate

                val boxCenterX = boxSize.width / 2f
                val boxCenterY = boxSize.height / 2f

                if (centroid == null) return@performFocusedTransformation

                val centroidX = (centroid.first.toFloat() / survey.size.first) * boxSize.width
                val centroidY = (centroid.second.toFloat() / survey.size.second) * boxSize.height

                var targetOffsetX = boxCenterX - centroidX
                var targetOffsetY = boxCenterY - centroidY

                val angleRad = Math.toRadians(rotation.toDouble()).toFloat()

                val rotatedOffsetX = (targetOffsetX * cos(angleRad) - targetOffsetY * sin(angleRad))
                val rotatedOffsetY = (targetOffsetX * sin(angleRad) + targetOffsetY * cos(angleRad))

                val focusedTranslation = Offset(rotatedOffsetX * zoom, rotatedOffsetY * zoom)

                offset = focusedTranslation
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

                    }
                )
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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { longPressLoc ->
                            val fractionalTapPosition =
                                calculateFractionalOffset(longPressLoc, boxSize)
                            longPressPosition(fractionalTapPosition)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = survey.imageBitmap(),
                contentDescription = survey.caveName,
                modifier = Modifier,
                contentScale = ContentScale.Fit
            )

            GraphOverlay(
                nodes = survey.pathNodes,
                modifier = Modifier.matchParentSize(),
                surveySize = IntSize(
                    width = survey.size.first,
                    height = survey.size.second
                ),
                currentRoute = currentRoute,
                destinationNode = pinpointDestinationNode,
                pinpointNode = pinpointSourceNode,
                currentRotation = rotation,
                currentZoom = zoom
            )
        }
    }
}

fun calculateAngle(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>): Double {
    val angle = Math.toDegrees(
        atan2(
            (coord2.second - coord1.second).toDouble(),
            (coord2.first - coord1.first).toDouble()
        )
    )

    return angle
}

fun performFocusedTransformation(
    currentRoute: Route,
    nodes: List<SurveyNode>,
    calculatedTransformation: (Float, Float, Pair<Int, Int>?) -> Unit
) {
    fun calculateDistance(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>) =
        kotlin.math.sqrt((coord2.second - coord1.second).toFloat() * (coord2.second - coord1.second).toFloat() + (coord2.first - coord1.first).toFloat() * (coord2.first - coord1.first).toFloat())

    fun calculateCentroid(currentStage: List<SurveyPath>): Pair<Int, Int>? {
        var cumulativeCentroidX = 0f
        var cumulativeCentroidY = 0f


        for (path in currentStage) {
            val firstNode = nodes.find { it.id == path.ends.first } ?: return null
            val secondNode = nodes.find { it.id == path.ends.second } ?: return null
            cumulativeCentroidX += (firstNode.coordinates.first + secondNode.coordinates.first) / 2
            cumulativeCentroidY += (firstNode.coordinates.second + secondNode.coordinates.second) / 2
        }

        return Pair(
            cumulativeCentroidX.toInt() / currentStage.size,
            cumulativeCentroidY.toInt() / currentStage.size
        )

    }

    val currentStage = currentRoute.getCurrentStage()
    val startNode = nodes.find { it.id == currentRoute.getCurrentStartingNode() }
    val endNode = nodes.find { it.id == currentRoute.getCurrentEndingNode() }

    if (startNode == null || endNode == null) return

    val finalAngle = calculateAngle(startNode.coordinates, endNode.coordinates)

    val centroid = calculateCentroid(currentStage)

    val finalDistance = calculateDistance(startNode.coordinates, endNode.coordinates)

    calculatedTransformation(finalAngle.toFloat(), finalDistance, centroid)
}

fun calculateFractionalOffset(tapLoc: Offset, size: IntSize): Offset {
    return Offset(
        x = (tapLoc.x / size.width.toFloat()),
        y = (tapLoc.y / size.height.toFloat())
    )
}

@Composable
fun GraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    surveySize: IntSize,
    currentRoute: Route? = null,
    pinpointNode: Int? = null,
    destinationNode: Int? = null,
    currentRotation: Float,
    currentZoom: Float
) {
    val pinpointIcon = ImageBitmap.imageResource(id = R.drawable.location_icon)
    val destinationIcon = ImageBitmap.imageResource(id = R.drawable.destination_icon)
    val currentDirectionIcon = ImageBitmap.imageResource(id = R.drawable.direction_icon)
    val arrowHead = ImageBitmap.imageResource(id = R.drawable.arrow_head)

    val adjustedZoom = if (currentZoom == 0f) 0.000001f else currentZoom

    Canvas(modifier = modifier) {
        currentRoute?.routeList?.forEachIndexed { index, pathList ->
            if (currentRoute.currentStage != -1 && index == currentRoute.currentStage) {
                currentRoute.getCurrentStage().forEach { path ->
                    val startNode = nodes.find { it.id == path.ends.first }!!
                    val endNode = nodes.find { it.id == path.ends.second }!!

                    drawLine(
                        color = Color(0xFF00B126),
                        start = Offset(
                            (startNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (startNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        end = Offset(
                            (endNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (endNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            } else {
                pathList.forEach { path ->
                    val startNode = nodes.find { it.id == path.ends.first }!!
                    val endNode = nodes.find { it.id == path.ends.second }!!

                    drawLine(
                        color = if (path.hasWater) Color(0xFF007ADD) else Color(0xFFA3A3A3),
                        start = Offset(
                            (startNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (startNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        end = Offset(
                            (endNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (endNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        if (pinpointNode != null && (currentRoute == null || currentRoute.routeStarted == false)) {
            val node = nodes.find { it.id == pinpointNode }
            if (node == null) return@Canvas

            val iconOffsetX = pinpointIcon.width
            val iconOffsetY = pinpointIcon.height.toFloat() * 2

            val pinPointCoodsX = (node.coordinates.first - iconOffsetX).toFloat()
            val pinPointCoodsY = (node.coordinates.second - iconOffsetY).toFloat()

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
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )
                    rotate(
                        degrees = -currentRotation,
                        pivot = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
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

        if (currentRoute?.routeStarted == true) {
            val currentStartNode = nodes.find { it.id == currentRoute.getCurrentStartingNode() }
            if (currentStartNode == null) {
                return@Canvas
            }
            val currentPathEndNode =
                currentRoute.getCurrentStage().first().next(currentStartNode.id)

            val currentStartAngle = calculateAngle(
                currentStartNode.coordinates,
                nodes.find { it.id == currentPathEndNode }!!.coordinates
            ).toFloat()

            val adjustedAngle = currentStartAngle + 90

            val iconOffsetX = currentDirectionIcon.width.toFloat()
            val iconOffsetY = currentDirectionIcon.height.toFloat()

            val pinPointCoodsX = (currentStartNode.coordinates.first - iconOffsetX).toFloat()
            val pinPointCoodsY = (currentStartNode.coordinates.second - iconOffsetY).toFloat()

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
                            (currentStartNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (currentStartNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )

                    rotate(
                        degrees = adjustedAngle,
                        pivot = Offset(
                            (currentStartNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (currentStartNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )
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

            val currentEndNode = nodes.find { it.id == currentRoute.getCurrentEndingNode() }
            if (currentEndNode == null) return@Canvas
            val currentEndPathStartNode =
                currentRoute.getCurrentStage().last().next(currentEndNode.id)

            val currentEndAngle = calculateAngle(
                nodes.find { it.id == currentEndPathStartNode }!!.coordinates,
                currentEndNode.coordinates
            ).toFloat()

            val adjustedEndAngle = currentEndAngle + 90

            val arrowIconOffsetX = arrowHead.width.toFloat()
            val arrowIconOffsetY = arrowHead.height.toFloat() * 0.5

            val arrowPointCoodsX = (currentEndNode.coordinates.first - arrowIconOffsetX).toFloat()
            val arrowPointCoodsY = (currentEndNode.coordinates.second - arrowIconOffsetY).toFloat()

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
                            (currentEndNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (currentEndNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )

                    rotate(
                        degrees = adjustedEndAngle,
                        pivot = Offset(
                            (currentEndNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (currentEndNode.coordinates.second / surveySize.height.toFloat()) * size.height
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


        if (destinationNode != null) {
            val node = nodes.find { it.id == destinationNode }
            if (node == null) return@Canvas

            val iconOffsetX = 0
            val iconOffsetY = destinationIcon.height.toFloat() * 2

            val pinPointCoodsX = (node.coordinates.first - iconOffsetX).toFloat()
            val pinPointCoodsY = (node.coordinates.second - iconOffsetY).toFloat()

            withTransform(
                {
                    scale(
                        scaleX = 0.75f / adjustedZoom,
                        scaleY = 0.75f / adjustedZoom,
                        pivot = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )
                    rotate(
                        degrees = -currentRotation,
                        pivot = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
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

fun calculateLength(start: Pair<Int, Int>, end: Pair<Int, Int>): Float {
    val pixelsPerMeter = 14.600609f
    val xDiff = (end.first - start.first).toFloat()
    val yDiff = (end.second - start.second).toFloat()
    return kotlin.math.sqrt(xDiff * xDiff + yDiff * yDiff) / pixelsPerMeter
}


@Preview
@Composable
fun GraphOverlayPreview() {
    CaveRoutePlannerTheme {
        ImageWithGraphOverlay(
            survey = llSurvey,
            modifier = Modifier,
        )
    }
}