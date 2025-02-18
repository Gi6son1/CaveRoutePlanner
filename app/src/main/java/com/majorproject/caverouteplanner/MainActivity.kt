package com.majorproject.caverouteplanner

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.majorproject.caverouteplanner.ui.components.GetImageBitmap
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
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                coordinates ->
                boxSize = coordinates.size
            }
    ){
        Image(
            bitmap = GetImageBitmap(survey.imageReference),

            contentDescription = survey.caveName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }

    GraphOverlay(
        nodes = survey.pathNodes,
        paths = survey.paths,
        imageSize = IntSize(GetImageBitmap(survey.imageReference).width, GetImageBitmap(survey.imageReference).height),
    )
}

@Composable
fun GraphOverlay(
    nodes: List<SurveyNode>,
    paths: List<SurveyPath>,
    imageSize: IntSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = imageSize.width.toFloat()
        val height = imageSize.height.toFloat()


        paths.forEach { path ->
            val startNode = nodes.find { it == path.ends.first }
            val endNode = nodes.find { it == path.ends.second }

            if (startNode != null && endNode != null) {
                drawLine(
                    color = Color.Red,
                    start = Offset(
                        startNode.coordinates.first * width,
                        startNode.coordinates.second * height
                    ),
                    end = Offset(
                        endNode.coordinates.first * width,
                        endNode.coordinates.second * height
                    ),
                    strokeWidth = 5f
                )
            }
        }

        nodes.forEach { node ->
            drawCircle(
                color = if (node.isEntrance) Color.Green else Color.Blue,
                radius = 10f,
                center = Offset(node.coordinates.first * width, node.coordinates.second * height)
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