package com.majorproject.caverouteplanner

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.llSurvey
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaveRoutePlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageWithGraph(
                        survey = llSurvey,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageWithGraph(
    survey: Survey,
    modifier: Modifier = Modifier
){
    var scaledImageSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .wrapContentSize()
            .onGloballyPositioned { coordinates ->
                scaledImageSize = coordinates.size
            }
    ){
        Image(
            bitmap = survey.imageBitmap(),

            contentDescription = survey.caveName,
            modifier = Modifier.border(1.dp, Color.Green),
            contentScale = ContentScale.Fit
        )
        GraphOverlay(
            nodes = survey.pathNodes,
            paths = survey.paths,
            size = IntSize(survey.imageBitmap().width, survey.imageBitmap().height),
        )
    }


}

@Composable
fun GraphOverlay(
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    size: IntSize
) {

    Canvas(modifier = Modifier.border(5.dp, Color.Red)) {
        val imageWidth = size.width.toFloat()
        val imageHeight = size.height.toFloat()

        paths.forEach { path ->
            val startNode = nodes.find { it == path.ends.first }
            val endNode = nodes.find { it == path.ends.second }

            if (startNode != null && endNode != null) {
                drawLine(
                    color = Color.Red,
                    start = Offset(
                        startNode.coordinates.first * imageWidth,
                        startNode.coordinates.second * imageHeight
                    ),
                    end = Offset(
                        endNode.coordinates.first * imageWidth,
                        endNode.coordinates.second * imageHeight
                    ),
                    strokeWidth = 5f
                )
            }
        }

        nodes.forEach { node ->
            drawCircle(
                color = if (node.isEntrance) Color.Green else Color.Blue,
                radius = 10f,
                center = Offset(node.coordinates.first * imageWidth, node.coordinates.second * imageHeight)
            )
        }
    }
}

@Preview
@Composable
fun GraphOverlayPreview() {
    CaveRoutePlannerTheme {
        ImageWithGraph(
            survey = llSurvey,
            modifier = Modifier.fillMaxSize()
        )
    }
}