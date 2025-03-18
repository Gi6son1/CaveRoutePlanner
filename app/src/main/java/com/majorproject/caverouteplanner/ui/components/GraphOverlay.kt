package com.majorproject.caverouteplanner.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier,
    routeFinder: RouteFinder? = null,
    currentRoute: Route? = null,
    longPressPosition: (Offset) -> Unit = {},
    pinpointNode: Int? = null
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
        if (currentRoute != null) {
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
        }
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures(
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

                        Log.d("offsetmetrics", "offset: $offset")
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
                paths = survey.paths,
                modifier = Modifier.matchParentSize(),
                surveySize = IntSize(
                    width = survey.size.first,
                    height = survey.size.second
                ),
                routeFinder = routeFinder,
                displayWeights = true,
                currentRoute = currentRoute,
                pinpointNode = pinpointNode
            )
        }
    }
}

fun performFocusedTransformation(
    currentRoute: Route,
    nodes: List<SurveyNode>,
    calculatedTransformation: (Float, Float, Pair<Int, Int>?) -> Unit
) {
    fun calculateAngle(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>): Double {
        val angle = Math.toDegrees(
            atan2(
                (coord2.second - coord1.second).toDouble(),
                (coord2.first - coord1.first).toDouble()
            )
        )

        return angle
    }

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
    paths: List<SurveyPath>,
    surveySize: IntSize,
    routeFinder: RouteFinder? = null,
    currentRoute: Route? = null,
    displayWeights: Boolean = false,
    pinpointNode: Int? = null
) {
    val textRememberer = rememberTextMeasurer()

    val pinpointIcon = ImageBitmap.imageResource(id = R.drawable.location_icon)

    Canvas(modifier = modifier) {
        if (currentRoute != null) {
            currentRoute.routeList.forEachIndexed { index, pathList ->
                if (index == currentRoute.currentStage) {
                    currentRoute.getCurrentStage().forEach { path ->
                        val startNode = nodes.find { it.id == path.ends.first }!!
                        val endNode = nodes.find { it.id == path.ends.second }!!

                        drawLine(
                            color = Color.Green,
                            start = Offset(
                                (startNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                                (startNode.coordinates.second / surveySize.height.toFloat()) * size.height
                            ),
                            end = Offset(
                                (endNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                                (endNode.coordinates.second / surveySize.height.toFloat()) * size.height
                            ),
                            strokeWidth = 4f
                        )
                    }
                } else {
                    pathList.forEach { path ->
                        val startNode = nodes.find { it.id == path.ends.first }!!
                        val endNode = nodes.find { it.id == path.ends.second }!!

                        drawLine(
                            color = if (path.hasWater) Color.Blue else Color.Red,
                            start = Offset(
                                (startNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                                (startNode.coordinates.second / surveySize.height.toFloat()) * size.height
                            ),
                            end = Offset(
                                (endNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                                (endNode.coordinates.second / surveySize.height.toFloat()) * size.height
                            ),
                            strokeWidth = 4f
                        )
                    }
                }

            }

        } else if (routeFinder != null) {
            if (displayWeights) {
                routeFinder.costMap.forEach { (path, weight) ->

                    val startNode = nodes.find { it.id == path.ends.first }!!
                    val endNode = nodes.find { it.id == path.ends.second }!!
                    val textResult = textRememberer.measure(
                        String.format("%.2f", weight),
                        style = TextStyle(fontSize = 6.sp)
                    )

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(
                            (startNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (startNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        end = Offset(
                            (endNode.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (endNode.coordinates.second / surveySize.height.toFloat()) * size.height
                        ),
                        strokeWidth = 4f
                    )

                    drawText(
                        textLayoutResult = textResult,
                        color = Color.Red,
                        topLeft = Offset(
                            ((startNode.coordinates.first + endNode.coordinates.first) / surveySize.width.toFloat()) / 2 * size.width - textResult.size.width / 2,
                            ((startNode.coordinates.second + endNode.coordinates.second) / surveySize.height.toFloat()) / 2 * size.height - textResult.size.height / 2
                        ),
                    )
                }

            }
        }
        if (pinpointNode != null) {
            val node = nodes.find { it.id == pinpointNode }
            if (node == null) return@Canvas

            val iconOffsetX = pinpointIcon.width
            val iconOffsetY = pinpointIcon.height.toFloat() * 2
            Log.d("pinpoint", "pinpointOffset: $iconOffsetX, $iconOffsetY")

            val pinPointCoodsX = (node.coordinates.first - iconOffsetX).toFloat()
            val pinPointCoodsY = (node.coordinates.second - iconOffsetY).toFloat()

            withTransform(
                {

                    translate(
                        left = 2f,
                        top = 2f
                    )
                    scale(
                        scaleX = 0.5f,
                        scaleY = 0.5f,
                        pivot = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )
                }
            ){
                drawImage(
                    image = pinpointIcon,
                    topLeft = Offset(
                        (pinPointCoodsX / surveySize.width.toFloat()) * size.width,
                        (pinPointCoodsY / surveySize.height.toFloat()) * size.height
                    )
                )
            }

            drawCircle(
                color = if (node.isEntrance) {
                    Color.Green
                } else if (node.isJunction) {
                    Color.Red
                } else {
                    Color.LightGray
                },
                radius = 2f,
                center = Offset(
                    (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                    (node.coordinates.second / surveySize.height.toFloat()) * size.height
                )
            )

            Log.d("pinpoint", "pinpoint: $pinpointNode")
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
            modifier = Modifier
        )
    }
}