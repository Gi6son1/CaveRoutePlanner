package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlin.math.abs

/**
 * Utilities file for getting the nearest node or path to a point
 */

/**
 * Function to get the nearest node to a point
 * @param pointCoordinates The coordinates of the point
 * @param nodes The list of nodes to inspect
 * @param surveyWidth The width of the survey for reference purposes
 * @param surveyHeight The height of the survey for reference purposes
 * @param searchBoundary The distance boundary to search for the nearest node
 * @return The nearest node to the point
 *
 */
fun getNearestNode(pointCoordinates: Offset, nodes: List<SurveyNode>, surveyWidth: Int, surveyHeight: Int, searchBoundary: Float): SurveyNode? {
    val convertedX = pointCoordinates.x * surveyWidth
    val convertedY = pointCoordinates.y * surveyHeight

    var nearestNode: SurveyNode? = null
    var nearestDistance = Float.MAX_VALUE
    nodes.forEach { node ->
        val distance = calculateDistance(Pair(node.x, node.y), Pair(convertedX.toInt(), convertedY.toInt())) //calculate the distance between the point and the node
        if (distance < nearestDistance && (distance < (surveyHeight.toFloat() + (surveyWidth.toFloat()) /2) * searchBoundary)) { //if the distance is less than the nearest distance, and within the search boundary, set the nearest node to the current node
            nearestNode = node
            nearestDistance = distance
        }
    }
    return nearestNode
}

/**
 * Function to get the nearest path to a point
 * @param pointCoordinates The coordinates of the point
 * @param nodes The list of nodes to inspect
 * @param paths The list of paths to inspect
 * @param surveyWidth The width of the survey for reference purposes
 * @param surveyHeight The height of the survey for reference purposes
 * @param ratioDifferenceThreshold The threshold for the ratio difference between the distance of the path and the distance of the nodes on each end of the path
 */
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
        val firstNodeDistance = calculateDistance(Pair(firstNode.x, firstNode.y), Pair(convertedX.toInt(), convertedY.toInt())) //calculate the distance between the point and the end node
        val secondNodeDistance = calculateDistance(Pair(secondNode.x, secondNode.y), Pair(convertedX.toInt(), convertedY.toInt())) //calculate the distance between the point and the other end node

        val distanceRatio = (firstNodeDistance + secondNodeDistance) / pathDistance //calculate the ratio of the distance of the path to the distance of the nodes on each end of the path
        val distanceRatioDifference = abs(distanceRatio - 1)

        if ((distanceRatioDifference < closestRatioDifference) && (distanceRatioDifference < ratioDifferenceThreshold)) { //if the difference is less than the closest difference, and within the threshold, set the closest path to the current path
            closestPath = path
            closestRatioDifference = distanceRatioDifference
        }
    }
    return closestPath
}