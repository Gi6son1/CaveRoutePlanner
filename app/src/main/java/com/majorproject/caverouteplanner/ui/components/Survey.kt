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
    var pixelsPerMeter: Float
    //1991×1429
){
    @Composable
    fun imageBitmap(): ImageBitmap {
        return ImageBitmap.imageResource(imageReference)
    }
}

val llSurvey = Survey(
    caveName = "LL",
    pixelsPerMeter = 14.600609f,
    imageReference = R.drawable.llygadlchwr,
    pathNodes = mutableListOf(
        SurveyNode(
            id = 7,
            isEntrance = true,
            coordinates = Pair(368 , 85),
            edges = mutableListOf(0),
            isJunction = true
        ),
        SurveyNode(
            id = 8,
            coordinates = Pair(332 , 144),
            edges = mutableListOf(0,1)
        ),
        SurveyNode(
            id = 9,
            coordinates = Pair(332 , 183),
            edges = mutableListOf(1,2)
        ),
        SurveyNode(
            id = 10,
            coordinates = Pair(388 , 189),
            edges = mutableListOf(2,3)
        ),
        SurveyNode(
            id = 11,
            coordinates = Pair(332 , 400),
            edges = mutableListOf(6,7)
        ),
        SurveyNode(
            id = 12,
            coordinates = Pair(464 , 400),
            edges = mutableListOf(8,9)
        ),
        SurveyNode(
            id = 13,
            coordinates = Pair(370 , 279),
            edges = mutableListOf(3,4,7),
            isJunction = true
        ),
        SurveyNode(
            id = 14,
            coordinates = Pair(410 , 300),
            edges = mutableListOf(4,5),
        ),
        SurveyNode(
            id = 15,
            coordinates = Pair(396 , 397),
            edges = mutableListOf(5,6,8),
            isJunction = true
        ),
        SurveyNode(
            id = 16,
            coordinates = Pair(464 , 443),
            edges = mutableListOf(9,10,11),
            isJunction = true
        ),
        SurveyNode(
            id = 17,
            coordinates = Pair(464 , 480),
            edges = mutableListOf(10,12,41),
            isJunction = true
        ),
        SurveyNode(
            id = 18,
            coordinates = Pair(494 , 429),
            edges = mutableListOf(11,13,15),
            isJunction = true
        ),
        SurveyNode(
            id = 19,
            coordinates = Pair(520 , 440),
            edges = mutableListOf(13,14),
        ),
        SurveyNode(
            id = 20,
            coordinates = Pair(500 , 470),
            edges = mutableListOf(12,14)
        ),
        SurveyNode(
            id = 21,
            coordinates = Pair(405 , 510),
            edges = mutableListOf(40,41)
        ),
        SurveyNode(
            id = 22,
            coordinates = Pair(370 , 730),
            edges = mutableListOf(39,40)
        ),
        SurveyNode(
            id = 23,
            coordinates = Pair(450 , 800),
            edges = mutableListOf(38,39)
        ),
        SurveyNode(
            id = 24,
            coordinates = Pair(525 , 830),
            edges = mutableListOf(36,37,38),
            isJunction = true
        ),
        SurveyNode(
            id = 25,
            coordinates = Pair(530 , 900),
            edges = mutableListOf(37),
        ),
        SurveyNode(
            id = 26,
            coordinates = Pair(630 , 800),
            edges = mutableListOf(35,36),
        ),
        SurveyNode(
            id = 27,
            coordinates = Pair(700 , 760),
            edges = mutableListOf(34,35),
        ),
        SurveyNode(
            id = 28,
            coordinates = Pair(765 , 760),
            edges = mutableListOf(33,34),
        ),
        SurveyNode(
            id = 29,
            coordinates = Pair(815 , 815),
            edges = mutableListOf(31,32,33),
            isJunction = true
        ),
        SurveyNode(
            id = 30,
            coordinates = Pair(775 , 895),
            edges = mutableListOf(32,59,60,62),
            isJunction = true
        ),
        SurveyNode(
            id = 31,
            coordinates = Pair(733 , 920),
            edges = mutableListOf(63),
        ),
        SurveyNode(
            id = 32,
            coordinates = Pair(740 , 893),
            edges = mutableListOf(62,63),
        ),
        SurveyNode(
            id = 33,
            coordinates = Pair(820 , 908),
            edges = mutableListOf(60,61),
        ),
        SurveyNode(
            id = 34,
            coordinates = Pair(820 , 945),
            edges = mutableListOf(61),
        ),
        SurveyNode(
            id = 35,
            coordinates = Pair(890 , 800),
            edges = mutableListOf(29,30,31),
            isJunction = true
        ),
        SurveyNode(
            id = 36,
            coordinates = Pair(1000 , 840),
            edges = mutableListOf(30,82,94),
            isJunction = true
        ),
        SurveyNode(
            id = 37,
            coordinates = Pair(890 , 600),
            edges = mutableListOf(28,29),
        ),
        SurveyNode(
            id = 38,
            coordinates = Pair(810 , 590),
            edges = mutableListOf(27,28),
        ),
        SurveyNode(
            id = 39,
            coordinates = Pair(810 , 560),
            edges = mutableListOf(26,27,42),
            isJunction = true
        ),
        SurveyNode(
            id = 40,
            coordinates = Pair(750 , 530),
            edges = mutableListOf(25,26),
        ),
        SurveyNode(
            id = 41,
            coordinates = Pair(670 , 530),
            edges = mutableListOf(24,25),
        ),
        SurveyNode(
            id = 42,
            coordinates = Pair(638 , 505),
            edges = mutableListOf(23,24),
        ),
        SurveyNode(
            id = 43,
            coordinates = Pair(620 , 280),
            edges = mutableListOf(21,22),
        ),
        SurveyNode(
            id = 44,
            coordinates = Pair(625 , 380),
            edges = mutableListOf(22,23),
        ),
        SurveyNode(
            id = 45,
            coordinates = Pair(620 , 200),
            edges = mutableListOf(20,21),
        ),
        SurveyNode(
            id = 46,
            coordinates = Pair(550 , 185),
            edges = mutableListOf(17,20),
        ),
        SurveyNode(
            id = 47,
            coordinates = Pair(500 , 205),
            edges = mutableListOf(16, 17, 18),
            isJunction = true
        ),
        SurveyNode(
            id = 48,
            coordinates = Pair(495 , 280),
            edges = mutableListOf(15,16),
        ),
        SurveyNode(
            id = 49,
            coordinates = Pair(490 , 180),
            edges = mutableListOf(18,19),
        ),
        SurveyNode(
            id = 50,
            coordinates = Pair(280 , 75),
            edges = mutableListOf(19),
        ),
        SurveyNode(
            id = 51,
            coordinates = Pair(890 , 535),
            edges = mutableListOf(42,43),
        ),
        SurveyNode(
            id = 52,
            coordinates = Pair(1050 , 535),
            edges = mutableListOf(43,44),
        ),
        SurveyNode(
            id = 53,
            coordinates = Pair(1075 , 565),
            edges = mutableListOf(44,45),
        ),
        SurveyNode(
            id = 54,
            coordinates = Pair(1100 , 600),
            edges = mutableListOf(45,46),
        ),
        SurveyNode(
            id = 55,
            coordinates = Pair(1090, 685),
            edges = mutableListOf(46,47),
        ),
        SurveyNode(
            id = 56,
            coordinates = Pair(1075 , 920),
            edges = mutableListOf(47,48,80),
            isJunction = true
        ),
        SurveyNode(
            id = 57,
            coordinates = Pair(1050 , 950),
            edges = mutableListOf(48,49,67),
            isJunction = true
        ),
        SurveyNode(
            id = 58,
            coordinates = Pair(990 , 930),
            edges = mutableListOf(49,50,94),
            isJunction = true
        ),
        SurveyNode(
            id = 59,
            coordinates = Pair(930 , 955),
            edges = mutableListOf(50,51),
        ),
        SurveyNode(
            id = 60,
            coordinates = Pair(893 , 1070),
            edges = mutableListOf(51,52,64),
            isJunction = true
        ),
        SurveyNode(
            id = 61,
            coordinates = Pair(850 , 1045),
            edges = mutableListOf(52,53),
        ),
        SurveyNode(
            id = 62,
            coordinates = Pair(820 , 1060),
            edges = mutableListOf(53,54),
        ),
        SurveyNode(
            id = 63,
            coordinates = Pair(785 , 1047),
            edges = mutableListOf(54,55),
        ),
        SurveyNode(
            id = 64,
            coordinates = Pair(742 , 1070),
            edges = mutableListOf(55,56,57),
            isJunction = true
        ),
        SurveyNode(
            id = 65,
            coordinates = Pair(700 , 1230),
            edges = mutableListOf(56),
        ),
        SurveyNode(
            id = 66,
            coordinates = Pair(742 , 1020),
            edges = mutableListOf(57,58),
        ),
        SurveyNode(
            id = 67,
            coordinates = Pair(767 , 960),
            edges = mutableListOf(58,59),
        ),
        SurveyNode(
            id = 68,
            coordinates = Pair(925 , 1095),
            edges = mutableListOf(64,65),
        ),
        SurveyNode(
            id = 69,
            coordinates = Pair(970 , 1030),
            edges = mutableListOf(65,66),
        ),
        SurveyNode(
            id = 70,
            coordinates = Pair(1050 , 1045),
            edges = mutableListOf(66,67,68),
            isJunction = true
        ),
        SurveyNode(
            id = 71,
            coordinates = Pair(1100 , 1065),
            edges = mutableListOf(68,69),
        ),
        SurveyNode(
            id = 72,
            coordinates = Pair(1085 , 1150),
            edges = mutableListOf(69,70),
        ),
        SurveyNode(
            id = 2,
            coordinates = Pair(1105 , 1165),
            edges = mutableListOf(70,71)
        ),
        SurveyNode(
            id = 3,
            coordinates = Pair(1170 , 1120),
            edges = mutableListOf(71,72)
        ),
        SurveyNode(
            id = 1,
            coordinates = Pair(1220 , 950),
            edges = mutableListOf(72,73,77,79),
            isJunction = true
        ),
        SurveyNode(
            id = 4,
            coordinates = Pair(1253 , 860),
            edges = mutableListOf(76,77)
        ),
        SurveyNode(
            id = 5,
            coordinates = Pair(1245 , 810),
            edges = mutableListOf(75,76)
        ),
        SurveyNode(
            id = 6,
            coordinates = Pair(1185 , 855),
            edges = mutableListOf(74,75,78),
            isJunction = true
        ),
        SurveyNode(
            id = 0,
            coordinates = Pair(1185 , 900),
            edges = mutableListOf(73,74)
        ),
        SurveyNode(
            id = 73,
            coordinates = Pair(1140 , 840),
            edges = mutableListOf(78,80,81),
            isJunction = true
        ),
        SurveyNode(
            id = 74,
            coordinates = Pair(1100 , 810),
            edges = mutableListOf(81,82)
        ),
        SurveyNode(
            id = 75,
            coordinates = Pair(1310 , 950),
            edges = mutableListOf(79,83)
        ),
        SurveyNode(
            id = 76,
            coordinates = Pair(1460 , 980),
            edges = mutableListOf(83,84)
        ),
        SurveyNode(
            id = 77,
            coordinates = Pair(1500 , 980),
            edges = mutableListOf(85)
        ),
        SurveyNode(
            id = 78,
            coordinates = Pair(1480 , 980),
            edges = mutableListOf(84,85,86,89),
            isJunction = true
        ),
        SurveyNode(
            id = 79,
            coordinates = Pair(1700 , 1000),
            edges = mutableListOf(86,87,88),
            isJunction = true
        ),
        SurveyNode(
            id = 80,
            coordinates = Pair(1725 , 900),
            edges = mutableListOf(87),
        ),
        SurveyNode(
            id = 81,
            coordinates = Pair(1690 , 1070),
            edges = mutableListOf(88),
        ),
        SurveyNode(
            id = 82,
            coordinates = Pair(1470 , 1020),
            edges = mutableListOf(89,90),
        ),
        SurveyNode(
            id = 83,
            coordinates = Pair(1430 , 1275),
            edges = mutableListOf(90,91),
        ),
        SurveyNode(
            id = 84,
            coordinates = Pair(1460 , 1175),
            edges = mutableListOf(92,93),
        ),
        SurveyNode(
            id = 85,
            coordinates = Pair(1445 , 1260),
            edges = mutableListOf(91,92),
        ),
        SurveyNode(
            id = 86,
            coordinates = Pair(1450 , 1100),
            edges = mutableListOf(93),
        ),
    ),
    paths = mutableListOf(
        SurveyPath(
            id = 0,
            ends = Pair(7, 8),
            distance = 4.7337646f
        ),
        SurveyPath(
            id = 1,
            ends = Pair(8, 9),
            distance = 2.6711216f
        ),
        SurveyPath(
            id = 2,
            ends = Pair(9, 10),
            distance = 3.8574083f
        ),
        SurveyPath(
            id = 3,
            ends = Pair(10, 13),
            distance = 6.2862f
        ),
        SurveyPath(
            id = 4,
            ends = Pair(13, 14),
            distance = 3.0942154f
        ),
        SurveyPath(
            id = 5,
            ends = Pair(14, 15),
            distance = 6.7123985f
        ),
        SurveyPath(
            id = 6,
            ends = Pair(11, 15),
            distance = 4.388192f
        ),
        SurveyPath(
            id = 7,
            ends = Pair(13, 11),
            distance = 8.686395f
        ),
        SurveyPath(
            id = 8,
            ends = Pair(15, 12),
            distance = 4.6618705f
        ),
        SurveyPath(
            id = 9,
            ends = Pair(12, 16),
            distance = 2.9450827f
        ),
        SurveyPath(
            id = 10,
            ends = Pair(16, 17),
            distance = 2.5341408f
        ),
        SurveyPath(
            id = 11,
            ends = Pair(16, 18),
            distance = 2.2674322f
        ),
        SurveyPath(
            id = 12,
            ends = Pair(17, 20),
            distance = 2.5590086f
        ),
        SurveyPath(
            id = 13,
            ends = Pair(18, 19),
            distance = 1.9335624f
        ),
        SurveyPath(
            id = 14,
            ends = Pair(19, 20),
            distance = 2.4694526f
        ),
        SurveyPath(
            id = 15,
            ends = Pair(18, 48),
            distance = 10.205284f
        ),
        SurveyPath(
            id = 16,
            ends = Pair(48, 47),
            altitude = -1,
            hasWater = true,
            distance = 5.1481743f
        ),
        SurveyPath(
            id = 17,
            ends = Pair(47, 46),
            altitude = -1,
            hasWater = true,
            distance = 3.6883152f
        ),
        SurveyPath(
            id = 18,
            ends = Pair(47, 49),
            altitude = -1,
            hasWater = true,
            distance = 1.8441576f
        ),
        SurveyPath(
            id = 19,
            ends = Pair(49, 50),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 16.08064f
        ),
        SurveyPath(
            id = 20,
            ends = Pair(46, 45),
            altitude = -1,
            hasWater = true,
            distance = 4.9031587f
        ),
        SurveyPath(
            id = 21,
            ends = Pair(45, 43),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 5.4792237f
        ),
        SurveyPath(
            id = 22,
            ends = Pair(43, 44),
            altitude = -1,
            hasWater = true,
            distance = 6.8575854f
        ),
        SurveyPath(
            id = 23,
            ends = Pair(44, 42),
            altitude = -1,
            hasWater = true,
            distance = 8.607462f
        ),
        SurveyPath(
            id = 24,
            ends = Pair(42, 41),
            altitude = -1,
            hasWater = true,
            distance = 2.7812457f
        ),
        SurveyPath(
            id = 25,
            ends = Pair(41, 40),
            altitude = -1,
            hasWater = true,
            distance = 5.4792237f
        ),
        SurveyPath(
            id = 26,
            ends = Pair(40, 39),
            altitude = -1,
            hasWater = true,
            distance = 4.5944686f
        ),
        SurveyPath(
            id = 27,
            ends = Pair(39, 38),
            altitude = -1,
            hasWater = true,
            distance = 2.0547087f
        ),
        SurveyPath(
            id = 28,
            ends = Pair(38, 37),
            distance = 5.521864f
        ),
        SurveyPath(
            id = 29,
            ends = Pair(37, 35),
            distance = 13.698059f
        ),
        SurveyPath(
            id = 30,
            ends = Pair(35, 36),
            distance = 8.016583f
        ),
        SurveyPath(
            id = 31,
            ends = Pair(35, 29),
            distance = 5.2385f
        ),
        SurveyPath(
            id = 32,
            ends = Pair(29, 30),
            distance = 6.125958f
        ),
        SurveyPath(
            id = 33,
            ends = Pair(29, 28),
            distance = 5.090907f
        ),
        SurveyPath(
            id = 34,
            ends = Pair(28, 27),
            distance = 4.451869f
        ),
        SurveyPath(
            id = 35,
            ends = Pair(27, 26),
            distance = 5.521864f
        ),
        SurveyPath(
            id = 36,
            ends = Pair(26, 24),
            distance = 7.479253f
        ),
        SurveyPath(
            id = 37,
            ends = Pair(24, 25),
            distance = 4.8065357f
        ),
        SurveyPath(
            id = 38,
            ends = Pair(24, 23),
            distance = 5.532473f
        ),
        SurveyPath(
            id = 39,
            ends = Pair(23, 22),
            distance = 7.280618f
        ),
        SurveyPath(
            id = 40,
            ends = Pair(22, 21),
            distance = 15.257357f
        ),
        SurveyPath(
            id = 41,
            ends = Pair(21, 17),
            distance = 4.533313f
        ),
        SurveyPath(
            id = 42,
            ends = Pair(39, 51),
            hasWater = true,
            altitude = -1,
            distance = 5.7405324f
        ),
        SurveyPath(
            id = 43,
            ends = Pair(51, 52),
            hasWater = true,
            altitude = -1,
            distance = 10.958447f
        ),
        SurveyPath(
            id = 44,
            ends = Pair(52, 53),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 2.6746314f
        ),
        SurveyPath(
            id = 45,
            ends = Pair(53, 54),
            hasWater = true,
            altitude = -1,
            distance = 2.945879f
        ),
        SurveyPath(
            id = 46,
            ends = Pair(54, 55),
            hasWater = true,
            altitude = -1,
            distance = 5.861825f
        ),
        SurveyPath(
            id = 47,
            ends = Pair(55, 56),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 16.127974f
        ),
        SurveyPath(
            id = 48,
            ends = Pair(56, 57),
            hasWater = true,
            altitude = -1,
            distance = 2.6746314f
        ),
        SurveyPath(
            id = 49,
            ends = Pair(57, 58),
            hasWater = true,
            altitude = -1,
            distance = 4.3317065f
        ),
        SurveyPath(
            id = 50,
            ends = Pair(58, 59),
            hasWater = true,
            altitude = -1,
            distance = 4.451869f
        ),
        SurveyPath(
            id = 51,
            ends = Pair(59, 60),
            hasWater = true,
            altitude = -1,
            distance = 8.274013f
        ),
        SurveyPath(
            id = 52,
            ends = Pair(60, 61),
            hasWater = true,
            altitude = -1,
            distance =  3.4066606f
        ),
        SurveyPath(
            id = 53,
            ends = Pair(61, 62),
            distance = 2.2972343f
        ),
        SurveyPath(
            id = 54,
            ends = Pair(62, 63),
            distance = 2.5571747f
        ),
        SurveyPath(
            id = 55,
            ends = Pair(63, 64),
            distance = 3.3399115f
        ),
        SurveyPath(
            id = 56,
            ends = Pair(64, 65),
            distance = 11.32971f
        ),
        SurveyPath(
            id = 57,
            ends = Pair(64, 66),
            distance = 3.4245148f
        ),
        SurveyPath(
            id = 58,
            ends = Pair(66, 67),
            distance = 4.451869f
        ),
        SurveyPath(
            id = 59,
            ends = Pair(67, 30),
            distance = 4.4854608f
        ),
        SurveyPath(
            id = 60,
            ends = Pair(30, 33),
            distance = 3.2080958f
        ),
        SurveyPath(
            id = 61,
            ends = Pair(33, 34),
            distance = 2.5341408f
        ),
        SurveyPath(
            id = 62,
            ends = Pair(30, 32),
            distance = 2.4010708f
        ),
        SurveyPath(
            id = 63,
            ends = Pair(32 , 31),
            distance = 1.9103758f
        ),
        SurveyPath(
            id = 64,
            ends = Pair(60, 68),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 2.7812457f
        ),
        SurveyPath(
            id = 65,
            ends = Pair(68, 69),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 5.414633f
        ),
        SurveyPath(
            id = 66,
            ends = Pair(69, 70),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 5.574706f
        ),
        SurveyPath(
            id = 67,
            ends = Pair(70, 57),
            altitude = -1,
            hasWater = true,
            distance = 6.506578f
        ),
        SurveyPath(
            id = 68,
            ends = Pair(70, 71),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 3.6883152f
        ),
        SurveyPath(
            id = 69,
            ends = Pair(71, 72),
            altitude = -1,
            hasWater = true,
            isHardTraverse = true,
            distance = 5.911629f
        ),
        SurveyPath(
            id = 70,
            ends = Pair(72, 2),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 1.7122574f
        ),
        SurveyPath(
            id = 71,
            ends = Pair(2, 3),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 5.414633f
        ),
        SurveyPath(
            id = 72,
            ends = Pair(3, 1),
            hasWater = true,
            altitude = -1,
            distance = 12.136512f
        ),
        SurveyPath(
            id = 73,
            ends = Pair(1, 0),
            distance = 4.180153f
        ),
        SurveyPath(
            id = 74,
            ends = Pair(0, 6),
            distance = 3.0820632f
        ),
        SurveyPath(
            id = 75,
            ends = Pair(6, 5),
            distance = 5.136772f
        ),
        SurveyPath(
            id = 76,
            ends = Pair(5, 4),
            distance = 3.4680715f
        ),
        SurveyPath(
            id = 77,
            ends = Pair(4, 1),
            distance = 6.5654297f
        ),
        SurveyPath(
            id = 78,
            ends = Pair(73, 6),
            distance = 3.24878f
        ),
        SurveyPath(
            id = 80,
            ends = Pair(73, 56),
            distance = 7.0598183f
        ),
        SurveyPath(
            id = 81,
            ends = Pair(73, 74),
            distance = 3.4245148f
        ),
        SurveyPath(
            id = 82,
            ends = Pair(74, 36),
            distance = 7.150597f
        ),
        SurveyPath(
            id = 79,
            ends = Pair(75, 1),
            hasWater = true,
            altitude = -1,
            distance = 6.1641264f
        ),
        SurveyPath(
            id = 83,
            ends = Pair(75, 76),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 10.477f
        ),
        SurveyPath(
            id = 84,
            ends = Pair(76, 78),
            distance = 1.3698059f
        ),
        SurveyPath(
            id = 85,
            ends = Pair(78, 77),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 1.3698059f
        ),
        SurveyPath(
            id = 86,
            ends = Pair(78, 79),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 15.130001f
        ),
        SurveyPath(
            id = 87,
            ends = Pair(79, 80),
            hasWater = true,
            altitude = -1,
            distance = 7.0598183f
        ),
        SurveyPath(
            id = 88,
            ends = Pair(79, 81),
            hasWater = true,
            altitude = -1,
            distance = 4.842995f
        ),
        SurveyPath(
            id = 89,
            ends = Pair(78, 82),
            distance = 2.8239272f
        ),
        SurveyPath(
            id = 90,
            ends = Pair(82, 83),
            hasWater = true,
            altitude = -1,
            isHardTraverse = true,
            distance = 17.67859f
        ),
        SurveyPath(
            id = 91,
            ends = Pair(83, 85),
            altitude = -1,
            distance = 1.4528985f
        ),
        SurveyPath(
            id = 92,
            ends = Pair(85, 84),
            distance = 5.911629f
        ),
        SurveyPath(
            id = 93,
            ends = Pair(84, 86),
            altitude = 1,
            distance = 5.182231f
        ),
        SurveyPath(
            id = 94,
            ends = Pair(58, 36),
            distance = 6.2020597f
        )
    )
)

