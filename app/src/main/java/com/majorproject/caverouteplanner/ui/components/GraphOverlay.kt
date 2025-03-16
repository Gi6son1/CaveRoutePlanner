package com.majorproject.caverouteplanner.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.components.performFocusedTransformation
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import kotlin.compareTo
import kotlin.div
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sign
import kotlin.text.toFloat
import kotlin.times

@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier,
    routeFinder: RouteFinder? = null,
    currentRoute: Route? = null,
    longPressPosition: (Offset) -> Unit = {}
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    var focusedRotation by remember { mutableFloatStateOf(0f) }
    var focusedTranslation by remember { mutableStateOf(Offset.Zero) }
    var focusedZoom by remember { mutableFloatStateOf(1f) }


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
                currentRoute.getCurrentStage(),
                survey.pathNodes,
                currentRoute.getCurrentStartingNode()
            ) { finalGradient, finalDistance, centroid ->

                val rotate = finalGradient + 90

                rotation = 0f
                focusedRotation = rotate

                val referenceDimension = maxOf(survey.size.first, survey.size.second).toFloat()

                val fractionalZoom = finalDistance / referenceDimension
                Log.d("metrics", "fractional zoom: $fractionalZoom")

                zoom = 1f
                focusedZoom = 1f / fractionalZoom

                val centroidx = (centroid.first / survey.size.first.toFloat()) * boxSize.width
                val centroidy = (centroid.second / survey.size.second.toFloat()) * boxSize.height
                Log.d("metrics", "centroid: $centroidx, $centroidy")

            }
        }
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, newZoom, newRotate ->
                        val adjustedScale = (zoom * newZoom).coerceIn(1f / focusedZoom, 20f / focusedZoom)

                        val boxScaledWidth = boxSize.width * adjustedScale * focusedZoom
                        val boxScaledHeight = boxSize.height * adjustedScale * focusedZoom

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

                        Log.d("metrics", "offset: $offset")
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
                    scaleX = zoom * focusedZoom,
                    scaleY = zoom * focusedZoom,
                    rotationZ = rotation + focusedRotation,
                    translationX = offset.x + focusedTranslation.x,
                    translationY = offset.y + focusedTranslation.y
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
                currentRoute = currentRoute
            )
        }
    }


}

fun performFocusedTransformation(
    currentStage: List<SurveyPath>?,
    nodes: List<SurveyNode>,
    startNode: Int,
    calculatedTransformation: (Float, Float, Pair<Int, Int>) -> Unit
) {
    fun calculateAngle(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>): Double {
        val angle = Math.toDegrees(
            atan2(
                (coord2.second - coord1.second).toDouble(),
                (coord2.first - coord1.first).toDouble()
            )
        )

        return if (angle < 0) {
            angle + 360
        } else {
            angle
        }
    }


    fun calculateDistance(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>) =
        kotlin.math.sqrt((coord2.second - coord1.second).toFloat() * (coord2.second - coord1.second).toFloat() + (coord2.first - coord1.first).toFloat() * (coord2.first - coord1.first).toFloat())

    fun calculateCentroid(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>) =
        Pair((coord1.first + coord2.first) / 2, (coord1.second + coord2.second) / 2)

    var combinedAngle = 0.0

    if (!currentStage.isNullOrEmpty()) {
        var startNode = nodes.find { it.id == startNode } ?: return
        var endNode: SurveyNode? = null

        var currentNode = startNode

        for (path in currentStage) {

            endNode = nodes.find { it.id == path.next(currentNode.id) } ?: return

            val angle = calculateAngle(startNode.coordinates, endNode.coordinates)
            combinedAngle += angle

            currentNode = endNode
        }

        if (endNode != null) {
            val finalGradient = combinedAngle / currentStage.size
            Log.d("metrics", finalGradient.toString())

            val finalDistance = calculateDistance(startNode.coordinates, endNode.coordinates)

            val centroid = calculateCentroid(startNode.coordinates, endNode.coordinates)

            calculatedTransformation(finalGradient.toFloat(), finalDistance, centroid)
        }
    }
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
    ) {
        val textRememberer = rememberTextMeasurer()
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
            } else {
                paths.forEach { path ->
                    val startNode = nodes.find { it.id == path.ends.first }!!
                    val endNode = nodes.find { it.id == path.ends.second }!!
                    val textResult = textRememberer.measure(
                        "${path.id}",
                        style = TextStyle(color = Color.Red, fontSize = 8.sp)
                    )

                    drawLine(
                        color = if (path.hasWater) Color.Blue else Color.LightGray,
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
                        color = Color.Magenta,
                        topLeft = Offset(
                            ((startNode.coordinates.first + endNode.coordinates.first) / surveySize.width.toFloat()) / 2 * size.width - textResult.size.width / 2,
                            ((startNode.coordinates.second + endNode.coordinates.second) / surveySize.height.toFloat()) / 2 * size.height - textResult.size.height / 2
                        ),
                    )


                }


                nodes.forEach { node ->

                    val textResult = textRememberer.measure(
                        "${node.id}",
                        style = TextStyle(color = Color.Red, fontSize = 6.sp)
                    )

                    drawCircle(
                        color = if (node.isEntrance) {
                            Color.Green
                        } else if (node.isJunction) {
                            Color.Red
                        } else {
                            Color.LightGray
                        },
                        radius = if (!node.isEntrance && !node.isJunction) 2f else 4f,
                        center = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
                        )
                    )

                    drawText(
                        textLayoutResult = textResult,
                        color = Color.Blue,
                        topLeft = Offset(
                            (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                            (node.coordinates.second / surveySize.height.toFloat()) * size.height
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
                modifier = Modifier
            )
        }
    }