package com.majorproject.caverouteplanner.ui.components

data class SurveyPath(
    var id: Int,
    var ends: Pair<Int, Int>,
    var distance: Double = 0.0,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false
)