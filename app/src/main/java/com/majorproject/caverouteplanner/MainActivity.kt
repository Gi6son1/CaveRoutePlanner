package com.majorproject.caverouteplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.llSurvey
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import moe.tlaster.zoomable.Zoomable
import moe.tlaster.zoomable.rememberZoomableState
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaveRoutePlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageWithGraphOverlay(
                        survey = llSurvey,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageWithGraphOverlay(
    survey: Survey,
    modifier: Modifier = Modifier
) {

    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale = max(scale * zoomChange, 1f)
        rotation += rotationChange
        offset += offsetChange
    }

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .transformable(state = state)

    ) {
        val surveyBox = createRef()
        Box(
            modifier = modifier
                .constrainAs(surveyBox){
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
                .border(2.dp, Color.Red)
                ,
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
                )
            )
        }
        val routeFinder = RouteFinder(7, survey)
    }
}



@Composable
fun GraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    surveySize: IntSize
) {
    val textRememberer = rememberTextMeasurer()
    Canvas(modifier = modifier.border(3.dp, Color.Green)) {

        paths.forEach { path ->
            val startNode = nodes.find { it.id == path.ends.first } !!
            val endNode = nodes.find { it.id == path.ends.second } !!
            val textResult = textRememberer.measure("%.2f".format(path.distance), style = TextStyle(color = Color. Red,fontSize = 8.sp))
            //val length : Float = calculateLength(startNode.coordinates, endNode.coordinates)

            //Log.d("Line Details", "Line ${path.id} from Node ${startNode.id} to Node ${endNode.id} has length $length")

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

/*
            drawText(
                textLayoutResult = textResult,
                color = Color.Magenta,
                topLeft = Offset(
                    ((startNode.coordinates.first + endNode.coordinates.first) / surveySize.width.toFloat()) / 2 * size.width - textResult.size.width/2,
                    ((startNode.coordinates.second + endNode.coordinates.second)/ surveySize.height.toFloat()) / 2 * size.height - textResult.size.height/2),
            )
*/

        }

        nodes.forEach { node ->

            val textResult = textRememberer.measure("${node.id}", style = TextStyle(color = Color. Red,fontSize = 6.sp))

            drawCircle(
                color = if (node.isEntrance) {
                    Color.Green
                } else if (node.isJunction){
                    Color.Red
                } else {
                    Color.LightGray
                },
                radius = if (!node.isEntrance && !node.isJunction) 2f else 4f,
                center = Offset(
                    (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                    (node.coordinates.second /surveySize.height.toFloat()) * size.height
                )
            )

            drawText(
                textLayoutResult = textResult,
                color = Color.Blue,
                topLeft = Offset(
                    (node.coordinates.first / surveySize.width.toFloat()) * size.width,
                    (node.coordinates.second /surveySize.height.toFloat()) * size.height
                )
            )




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