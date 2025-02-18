package com.majorproject.caverouteplanner.ui.components

data class SurveyNode(
    var id: Int = 0,
    var isEntrance: Boolean = false,
    var coordinates: Pair<Float, Float>,
    var edges: MutableList<SurveyPath>
) {
}