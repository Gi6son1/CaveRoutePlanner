package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromInternalStorage
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme


@Composable
fun CaveCardButton(
    cave: Cave,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    Button(
        onClick = { onClick() },
        contentPadding = PaddingValues(0.dp),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        elevation = ButtonDefaults.buttonElevation(10.dp)
    ) {
        CaveCard(cave)
    }
}

@Composable
fun CaveCard(cave: Cave) {
    Card(
        modifier = Modifier
            .width(500.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline
        )
    ) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(getBitmapFromInternalStorage(filePath = cave.surveyProperties.imageReference)) }

        Row {
            if (imageBitmap != null){
                Card {
                    Image(bitmap = imageBitmap!!, contentDescription = "Cave Survey",
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly){
                Text(text = cave.caveProperties.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.surface)
                Text(text = cave.caveProperties.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.inverseOnSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Length: ${cave.caveProperties.length}km", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.inverseOnSurface)
                    Spacer(modifier = Modifier.width(5.dp))
                    VerticalDivider(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "Difficulty: ${cave.caveProperties.difficulty}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.inverseOnSurface)
                }
            }
        }
    }
}

@Preview
@Composable
fun CaveCardPreview(
) {
    CaveRoutePlannerTheme {
        CaveCardButton(
            Cave(
                caveProperties = CaveProperties(
                    name = "Llygad Lchwr",
                    length = 1.2f,
                    description = "Contains dry high level and an active river level, separated by sumps.",
                    difficulty = "Novice",
                    location = "South Wales",
                    surveyId = 1
                ),
                surveyProperties = SurveyProperties(
                    width = 1991,
                    height = 1429,
                    pixelsPerMeter = 14.600609f,
                    imageReference = "llygadlchwr.jpg",
                    northAngle = 0f
                )
            )
        )
    }
}
