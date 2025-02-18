package com.majorproject.caverouteplanner.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.majorproject.caverouteplanner.R

data class Survey(
    var id: Int = 0,
    var caveName: String = "",
    var imageReference: Int,
    var pathNodes: MutableList<SurveyNode>,
    var paths: MutableList<SurveyPath>
){
    @Composable
    fun imageBitmap(): ImageBitmap {
        return ImageBitmap.imageResource(imageReference)
    }
}

val llSurvey = Survey(
    caveName = "LL",
    imageReference = R.drawable.llygadlchwr,
    pathNodes = mutableListOf(
        SurveyNode(
            isEntrance = false,
            coordinates = Pair(0.0f , 0.0f),
            edges = mutableListOf()
        ),
        SurveyNode(
            isEntrance = true,
            coordinates = Pair(0.5f , 0.5f),
            edges = mutableListOf()
        )
    ),
    paths = mutableListOf()
)

