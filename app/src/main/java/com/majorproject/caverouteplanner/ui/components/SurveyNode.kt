package com.majorproject.caverouteplanner.ui.components

data class SurveyNode(
    var id: Int,
    var isEntrance: Boolean = false,
    var isJunction: Boolean = false,
    var coordinates: Pair<Int, Int>,
    var edges: MutableList<Int>
)