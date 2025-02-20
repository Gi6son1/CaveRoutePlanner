package com.majorproject.caverouteplanner

import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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

@OptIn(ExperimentalFoundationApi::class)
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
                modifier = Modifier.matchParentSize()
            )
        }
    }

}



@Composable
fun GraphOverlay(
    modifier: Modifier = Modifier,
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>
) {
    Canvas(modifier = modifier.border(3.dp, Color.Green)) {
        paths.forEach { path ->
            val startNode = path.ends.first
            val endNode = path.ends.second

            drawLine(
                color = Color.Red,
                start = Offset(
                    startNode.coordinates.first * size.width,
                    startNode.coordinates.second * size.height
                ),
                end = Offset(
                    endNode.coordinates.first * size.width,
                    endNode.coordinates.second * size.height
                ),
                strokeWidth = 5f
            )
        }

        nodes.forEach { node ->
            drawCircle(
                color = if (node.isEntrance) Color.Green else Color.Blue,
                radius = 10f,
                center = Offset(
                    node.coordinates.first * size.width,
                    node.coordinates.second * size.height
                )
            )
        }
    }
}


@Preview
@Composable
fun GraphOverlayPreview() {
    CaveRoutePlannerTheme {
        ImageWithGraphOverlay(
            survey = llSurvey,
            modifier = Modifier.fillMaxSize()
        )
    }
}