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
    var paths: MutableList<SurveyPath>,
    //1991×1429
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
            coordinates = Pair(0 , 0),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 4,
            coordinates = Pair(1991 , 1429),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 5,
            isEntrance = false,
            coordinates = Pair(1991 , 0),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 6,
            coordinates = Pair(0 , 1429),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 7,
            isEntrance = true,
            coordinates = Pair(368 , 85),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 8,
            coordinates = Pair(332 , 144),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 9,
            coordinates = Pair(332 , 183),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 10,
            coordinates = Pair(388 , 189),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 11,
            coordinates = Pair(332 , 400),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 12,
            coordinates = Pair(464 , 400),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 13,
            coordinates = Pair(370 , 279),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 14,
            coordinates = Pair(410 , 300),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 15,
            coordinates = Pair(396 , 397),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 16,
            coordinates = Pair(464 , 443),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 17,
            coordinates = Pair(464 , 480),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 18,
            coordinates = Pair(494 , 429),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 19,
            coordinates = Pair(520 , 440),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 20,
            coordinates = Pair(500 , 470),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 21,
            coordinates = Pair(405 , 510),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 22,
            coordinates = Pair(370 , 730),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 23,
            coordinates = Pair(450 , 800),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 24,
            coordinates = Pair(525 , 830),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 25,
            coordinates = Pair(530 , 900),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 26,
            coordinates = Pair(630 , 800),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 27,
            coordinates = Pair(700 , 760),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 28,
            coordinates = Pair(765 , 760),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 29,
            coordinates = Pair(815 , 815),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 30,
            coordinates = Pair(775 , 895),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 31,
            coordinates = Pair(775 , 930),
            edges = mutableListOf(),
        ),
    ),
    paths = mutableListOf(
    )
)

