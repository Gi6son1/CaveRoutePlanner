package com.majorproject.caverouteplanner.ui.components

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromInternalStorage
import com.majorproject.caverouteplanner.navigation.Route
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun MarkupImageAndGraphOverlay(
    paths: List<SurveyPath>,
    nodes: List<SurveyNode>,
    surveyImage: ImageBitmap,
    modifier: Modifier = Modifier,
    longPressPosition: (Offset) -> Unit = {},
    onTapPosition: (Offset) -> Unit = {},
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
                    bitmap = surveyImage,
                    contentDescription = "survey",
                    modifier = Modifier,
                    contentScale = ContentScale.Fit
            )

            MarkupGraphOverlay(
                nodes = nodes,
                modifier = Modifier.matchParentSize(),
                surveySize = IntSize(
                    width = surveyImage.width,
                    height = surveyImage.height
                ),
                paths = paths
            )
        }
    }
}


@Composable
fun MarkupGraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    surveySize: IntSize,
) {
    Canvas(modifier = modifier) {
        for (node in nodes) {
            drawCircle(
                color = Color.Green,
                radius = 2f,
                center = Offset(
                    (node.x / surveySize.width.toFloat()) * size.width,
                    (node.y / surveySize.height.toFloat()) * size.height
                )
            )
        }

        for (path in paths) {
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
