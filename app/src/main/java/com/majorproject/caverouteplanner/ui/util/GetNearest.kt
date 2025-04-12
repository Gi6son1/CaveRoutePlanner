package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlin.math.abs
import kotlin.math.pow

fun getNearestNode(pointCoordinates: Offset, nodes: List<SurveyNode>, surveyWidth: Int, surveyHeight: Int, searchBoundary: Float): SurveyNode? {
    val convertedX = pointCoordinates.x * surveyWidth
    val convertedY = pointCoordinates.y * surveyHeight

    var nearestNode: SurveyNode? = null
    var nearestDistance = Float.MAX_VALUE
    nodes.forEach { node ->
        val distance = calculateDistance(Pair(node.x, node.y), Pair(convertedX.toInt(), convertedY.toInt()))
        if (distance < nearestDistance && (distance < (surveyHeight.toFloat() + (surveyWidth.toFloat()) /2) * searchBoundary)) {
            nearestNode = node
            nearestDistance = distance
        }
    }
    return nearestNode
}

fun getNearestLine(pointCoordinates: Offset, nodes: List<SurveyNode>, paths: List<SurveyPath>, surveyWidth: Int, surveyHeight: Int, ratioDifferenceThreshold: Float): SurveyPath?{
    val convertedX = pointCoordinates.x * surveyWidth
    val convertedY = pointCoordinates.y * surveyHeight

    var closestPath: SurveyPath? = null
    var closestRatioDifference = Float.MAX_VALUE

    paths.forEach { path ->
        val firstNode = nodes.find { it.getNodeId() == path.getPathEnds().first }
        val secondNode = nodes.find { it.getNodeId() == path.getPathEnds().second }
        if (firstNode == null || secondNode == null) return null

        val pathDistance = path.distance
        val firstNodeDistance = calculateDistance(Pair(firstNode.x, firstNode.y), Pair(convertedX.toInt(), convertedY.toInt()))
        val secondNodeDistance = calculateDistance(Pair(secondNode.x, secondNode.y), Pair(convertedX.toInt(), convertedY.toInt()))

        val distanceRatio = (firstNodeDistance + secondNodeDistance) / pathDistance
        val distanceRatioDifference = abs(distanceRatio - 1)

        if ((distanceRatioDifference < closestRatioDifference) && (distanceRatioDifference < ratioDifferenceThreshold)) {
            closestPath = path
            closestRatioDifference = distanceRatioDifference
        }
    }
    return closestPath
}