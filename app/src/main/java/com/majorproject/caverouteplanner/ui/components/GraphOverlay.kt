package com.majorproject.caverouteplanner.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import kotlin.math.max

@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier,
    routeFinder: RouteFinder? = null,
    currentRoute: Route? = null,
    longPressPosition: (Offset) -> Unit = {}
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = (scale * zoomChange).coerceIn(1f, 20f)
        scale = newScale
        rotation += rotationChange
        offset += offsetChange
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .transformable(state = state)
    ) {
        val (overlay) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(overlay) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
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
                            val fractionalTapPosition = calculateFractionalOffset(longPressLoc, boxSize)
                            Log.d("TAP", "onTap: ${fractionalTapPosition.x}, ${fractionalTapPosition.y}")
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
                    width = survey.imageBitmap().width,
                    height = survey.imageBitmap().height
                ),
                routeFinder = routeFinder,
                displayWeights = true,
                currentRoute = currentRoute
            )
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