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
            id = 1,
            isEntrance = false,
            coordinates = Pair(0.0f , 0.0f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 2,
            isEntrance = false,
            coordinates = Pair(0.53f , 0.451f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 3,
            isEntrance = true,
            coordinates = Pair(0.5f , 0.5f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 4,
            isEntrance = false,
            coordinates = Pair(1.0f , 1.0f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 5,
            isEntrance = false,
            coordinates = Pair(1.0f , 0f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 6,
            isEntrance = false,
            coordinates = Pair(0.0f , 1.0f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 7,
            isEntrance = true,
            coordinates = Pair(0.185f , 0.06f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 8,
            isEntrance = false,
            coordinates = Pair(0.167f , 0.101f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 9,
            isEntrance = false,
            coordinates = Pair(0.167f , 0.128f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 10,
            isEntrance = false,
            coordinates = Pair(0.195f , 0.132f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 11,
            isEntrance = false,
            coordinates = Pair(0.167f , 0.28f),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 12,
            isEntrance = false,
            coordinates = Pair(0.233f , 0.28f),
            edges = mutableListOf()
        ),

    ),
    paths = mutableListOf(
        SurveyPath(
            id = 1,
            ends = Pair(2, 3),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        ),
        SurveyPath(
            id = 2,
            ends = Pair(7, 8),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        ),
        SurveyPath(
            id = 3,
            ends = Pair(8, 9),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        ),
        SurveyPath(
            id = 4,
            ends = Pair(9, 10),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        ),
        SurveyPath(
            id = 5,
            ends = Pair(10, 11),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        ),
        SurveyPath(
            id = 5,
            ends = Pair(11, 12),
            distance = 1.0,
            hasWater = false,
            altitude = 0,
        )
    )
)

