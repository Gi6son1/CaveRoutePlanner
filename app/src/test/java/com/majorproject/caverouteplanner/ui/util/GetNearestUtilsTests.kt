package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class GetNearestLineTests{
    private val node0 = SurveyNode(x = 0, y = 0, surveyId = 1, id = 0)
    private val node1 = SurveyNode(x = 20, y = 0, surveyId = 1, id = 1)
    private val node2 = SurveyNode(x = 0, y = 20, surveyId = 1, id = 2)
    private val node3 = SurveyNode(x = 20, y = 20, surveyId = 1, id = 3)
    private val node4 = SurveyNode(x = 20, y = 0, surveyId = 1, id = 4)
    private val node5 = SurveyNode(x = 20, y = 20, surveyId = 1, id = 5)


    private val edge1 = SurveyPath(ends = Pair(0, 1), distance = 20.0f, surveyId = 1)
    private val edge2 = SurveyPath(ends = Pair(2, 3), distance = 20.0f, surveyId = 1)
    private val edge3 = SurveyPath(ends = Pair(4, 5), distance = 20.0f, surveyId = 1)
    private val edge4 = SurveyPath(ends = Pair(0, 2), distance = 28.3f, surveyId = 1)
    private val threshold = 0.5f
    private val targetPoint = Offset(0.5f, 0.5f)

    @Test
    fun getNearestLineReturnsNearestTest() {
        val edges = listOf(edge1, edge2, edge3)
        val nodes = listOf(node0, node1, node2, node3, node4, node5)

        val nearestEdge = getNearestLine(pointCoordinates = targetPoint, nodes = nodes, paths = edges, surveyWidth = 20, surveyHeight = 20, ratioDifferenceThreshold = threshold)

        assertEquals(edge1, nearestEdge)
    }

    @Test
    fun getNearestLineMultipleCloseReturnsNearestTest() {
        val edges = listOf(edge1, edge2, edge4)
        val nodes = listOf(node0, node1, node2, node3)

        val nearestEdge = getNearestLine(pointCoordinates = targetPoint, nodes = nodes, paths = edges, surveyWidth = 20, surveyHeight = 20, ratioDifferenceThreshold = threshold)

        assertEquals(edge4, nearestEdge)
    }

    @Test
    fun getNearestLineWithNoLinesReturnNull() {
        val edges = emptyList<SurveyPath>()
        val nodes = emptyList<SurveyNode>()

        val nearestEdge = getNearestLine(pointCoordinates = targetPoint, nodes = nodes, paths = edges, surveyWidth = 20, surveyHeight = 20, ratioDifferenceThreshold = threshold)

        assertNull(nearestEdge)
    }

    @Test
    fun getNearestLineWithTargetOnEdgeReturnEdge() {
        val edgeTargetPoint = Offset(0.5f, 0f)
        val edges = listOf(edge1)
        val nodes = listOf(node0, node1)

        val nearestEdge = getNearestLine(pointCoordinates = edgeTargetPoint, nodes = nodes, paths = edges, surveyWidth = 20, surveyHeight = 20, ratioDifferenceThreshold = threshold)

        assertEquals(edge1, nearestEdge)
    }

    @Test
    fun getNearestLineWithTargetOutOfBoundsReturnsNull() {
        val outOfBoundstargetPoint = Offset(2f, 2f)
        val edges = listOf(edge1)
        val nodes = listOf(node0, node1)

        val nearestEdge = getNearestLine(pointCoordinates = outOfBoundstargetPoint, nodes = nodes, paths = edges, surveyWidth = 20, surveyHeight = 20, ratioDifferenceThreshold = threshold)

        assertNull(nearestEdge)
    }
}

class GetNearestNodeTests(){
    private val node0 = SurveyNode(x = 0, y = 0, surveyId = 1)
    private val node1 = SurveyNode(x = 20, y = 0, surveyId = 1)
    private val node2 = SurveyNode(x = 0, y = 20, surveyId = 1)
    private val node3 = SurveyNode(x = 20, y = 20, surveyId = 1)
    private val node4 = SurveyNode(x = 20, y = 18, surveyId = 1)
    private val threshold = 0.3f
    private val targetPoint = Offset(0.5f, 0.5f)
    private val outOfBoundsTargetPoint = Offset(2f, 2f)
    val surveyWidth = 20
    val surveyHeight = 20

    @Test
    fun getNearestNodeReturnsNearestTest() {
        val closeTargetPoint = Offset(0.1f, 0.1f)
        val nodes = listOf(node0, node1, node2, node3)

        val nearestNode = getNearestNode(closeTargetPoint, nodes, surveyWidth, surveyHeight, threshold)

        assertEquals(node0, nearestNode)
    }

    @Test
    fun getNearestNodeMultipleCloseReturnsNearestTest() {
        val closeTargetPoint = Offset(1f, 0.95f)
        val nodes = listOf(node0, node1, node2, node3, node4)

        val nearestNode = getNearestNode(closeTargetPoint, nodes, surveyWidth, surveyHeight, threshold)

        assertEquals(node3, nearestNode)
    }

    @Test
    fun getNearestNodeWithNoNodesReturnNull() {
        val nodes = emptyList<SurveyNode>()

        val nearestNode = getNearestNode(targetPoint, nodes, surveyWidth, surveyHeight, threshold)

        assertNull(nearestNode)
    }

    @Test
    fun getNearestNodeWithTargetOnNodeReturnNode() {
        val onNodeTargetPoint = Offset(0f, 0f)
        val nodes = listOf(node0, node1, node2, node3)

        val nearestNode = getNearestNode(onNodeTargetPoint, nodes, surveyWidth, surveyHeight, threshold)

        assertEquals(node0, nearestNode)
    }

    @Test
    fun getNearestNodeWithTargetOutOfBoundsReturnsNull() {
        val nodes = listOf(node0, node1, node2, node3)

        val nearestNode = getNearestNode(outOfBoundsTargetPoint, nodes, surveyWidth, surveyHeight, threshold)

        assertNull(nearestNode)
    }
}