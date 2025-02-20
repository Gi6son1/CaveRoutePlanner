package com.majorproject.caverouteplanner.ui.components

data class SurveyPath(
    var id: Int = 0,
    var ends: Pair<Int, Int>,
    var distance: Double,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false
) {
}