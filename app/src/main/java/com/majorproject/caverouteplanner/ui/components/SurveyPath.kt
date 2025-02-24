package com.majorproject.caverouteplanner.ui.components

data class SurveyPath(
    var id: Int,
    var ends: Pair<Int, Int>,
    var distance: Float = 0.0f,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false
)