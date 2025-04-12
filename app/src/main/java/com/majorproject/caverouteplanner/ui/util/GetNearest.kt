package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import kotlin.math.pow

fun getNearestNode(pointCoordinates: Offset, nodes: List<SurveyNode>, surveyWidth: Int, surveyHeight: Int): SurveyNode? {
    val convertedX = pointCoordinates.x * surveyWidth
    val convertedY = pointCoordinates.y * surveyHeight
    val nearestNode = nodes.minByOrNull { (it.x - convertedX).pow(2) + (it.y - convertedY).pow(2) }
    return if (nearestNode != null) {
        val distance = (nearestNode.x - convertedX).pow(2) + (nearestNode.y - convertedY).pow(2)
        if (distance < (surveyHeight.toFloat().pow(2) + (surveyWidth.toFloat().pow(2)) /2) * 0.01)
            nearestNode
        else {
            null
        }
    } else {
        null
    }
}