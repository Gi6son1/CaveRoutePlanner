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
            isJunction = true
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
            coordinates = Pair(733 , 920),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 32,
            coordinates = Pair(740 , 893),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 33,
            coordinates = Pair(820 , 908),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 34,
            coordinates = Pair(820 , 945),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 35,
            coordinates = Pair(890 , 800),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 36,
            coordinates = Pair(1000 , 840),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 37,
            coordinates = Pair(890 , 600),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 38,
            coordinates = Pair(810 , 590),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 39,
            coordinates = Pair(810 , 560),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 40,
            coordinates = Pair(750 , 530),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 41,
            coordinates = Pair(670 , 530),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 42,
            coordinates = Pair(638 , 505),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 43,
            coordinates = Pair(620 , 280),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 44,
            coordinates = Pair(625 , 380),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 45,
            coordinates = Pair(620 , 200),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 46,
            coordinates = Pair(550 , 185),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 47,
            coordinates = Pair(500 , 205),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 48,
            coordinates = Pair(495 , 280),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 49,
            coordinates = Pair(490 , 180),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 50,
            coordinates = Pair(280 , 75),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 51,
            coordinates = Pair(890 , 535),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 52,
            coordinates = Pair(1050 , 535),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 53,
            coordinates = Pair(1075 , 565),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 54,
            coordinates = Pair(1100 , 600),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 55,
            coordinates = Pair(1090, 685),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 56,
            coordinates = Pair(1075 , 920),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 57,
            coordinates = Pair(1050 , 950),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 58,
            coordinates = Pair(990 , 930),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 59,
            coordinates = Pair(930 , 955),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 60,
            coordinates = Pair(893 , 1070),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 61,
            coordinates = Pair(850 , 1045),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 62,
            coordinates = Pair(820 , 1060),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 63,
            coordinates = Pair(785 , 1047),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 64,
            coordinates = Pair(742 , 1070),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 65,
            coordinates = Pair(700 , 1230),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 66,
            coordinates = Pair(742 , 1020),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 67,
            coordinates = Pair(767 , 960),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 68,
            coordinates = Pair(925 , 1095),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 69,
            coordinates = Pair(970 , 1030),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 70,
            coordinates = Pair(1050 , 1045),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 71,
            coordinates = Pair(1100 , 1065),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 72,
            coordinates = Pair(1085 , 1150),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 2,
            coordinates = Pair(1105 , 1165),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 3,
            coordinates = Pair(1170 , 1120),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 1,
            coordinates = Pair(1220 , 950),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 4,
            coordinates = Pair(1253 , 860),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 5,
            coordinates = Pair(1245 , 810),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 6,
            coordinates = Pair(1185 , 855),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 0,
            coordinates = Pair(1185 , 900),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 73,
            coordinates = Pair(1140 , 840),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 74,
            coordinates = Pair(1100 , 810),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 75,
            coordinates = Pair(1310 , 950),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 76,
            coordinates = Pair(1460 , 980),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 77,
            coordinates = Pair(1500 , 980),
            edges = mutableListOf()
        ),
        SurveyNode(
            id = 78,
            coordinates = Pair(1480 , 980),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 79,
            coordinates = Pair(1700 , 1000),
            edges = mutableListOf(),
            isJunction = true
        ),
        SurveyNode(
            id = 80,
            coordinates = Pair(1725 , 900),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 81,
            coordinates = Pair(1690 , 1070),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 82,
            coordinates = Pair(1470 , 1020),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 83,
            coordinates = Pair(1430 , 1275),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 84,
            coordinates = Pair(1460 , 1175),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 85,
            coordinates = Pair(1445 , 1260),
            edges = mutableListOf(),
        ),
        SurveyNode(
            id = 86,
            coordinates = Pair(1450 , 1100),
            edges = mutableListOf(),
        ),
    ),
    paths = mutableListOf(
        SurveyPath(
            id = 0,
            ends = Pair(7, 8),
        ),
        SurveyPath(
            id = 1,
            ends = Pair(8, 9),
        ),
        SurveyPath(
            id = 2,
            ends = Pair(9, 10),
        ),
        SurveyPath(
            id = 3,
            ends = Pair(10, 13),
        ),
        SurveyPath(
            id = 4,
            ends = Pair(13, 14),
        ),
        SurveyPath(
            id = 5,
            ends = Pair(14, 15),
        ),
        SurveyPath(
            id = 6,
            ends = Pair(11, 15),
        ),
        SurveyPath(
            id = 7,
            ends = Pair(13, 11),
        ),
        SurveyPath(
            id = 8,
            ends = Pair(15, 12),
        ),
        SurveyPath(
            id = 9,
            ends = Pair(12, 16),
        ),
        SurveyPath(
            id = 10,
            ends = Pair(16, 17),
        ),
        SurveyPath(
            id = 11,
            ends = Pair(16, 18),
        ),
        SurveyPath(
            id = 12,
            ends = Pair(17, 20),
        ),
        SurveyPath(
            id = 13,
            ends = Pair(18, 19),
        ),
        SurveyPath(
            id = 14,
            ends = Pair(19, 20),
        ),
        SurveyPath(
            id = 15,
            ends = Pair(18, 48),
        ),
        SurveyPath(
            id = 16,
            ends = Pair(48, 47),
            altitude = -1,
            hasWater = true
        ),
        SurveyPath(
            id = 17,
            ends = Pair(47, 46),
            altitude = -1,
            hasWater = true
        ),
        SurveyPath(
            id = 18,
            ends = Pair(47, 49),
            altitude = -1,
            hasWater = true
        ),
        SurveyPath(
            id = 19,
            ends = Pair(49, 50),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true
        )
    )
)

