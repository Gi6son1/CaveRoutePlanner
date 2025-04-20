package com.majorproject.caverouteplanner.navigation

import com.majorproject.caverouteplanner.ui.components.SurveyPath
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RouteTests{

    val edge1 = SurveyPath(ends = Pair(1, 2), distance = 10.5f, surveyId = 1)
    val edge2 = SurveyPath(ends = Pair(2, 3), distance = 15.2f, surveyId = 1)
    val edge3 = SurveyPath(ends = Pair(3, 4), distance = 20.8f, surveyId = 1)
    val edge4 = SurveyPath(ends = Pair(4, 5), distance = 25.1f, surveyId = 1)

    @Test
    fun routeCreatedWithSinglePathReturnsSinglePath() {
        val singlePathList = listOf(listOf(edge1))

        val route = Route(routeList = singlePathList, totalDistance = 0f, sourceNode = 0, numberOfTravellers = 1)
        route.beginJourney()

        assertEquals(singlePathList, route.routeList)
        assertEquals(listOf(edge1), route.getCurrentStage())
        assertEquals(edge1.getPathEnds().first, route.getCurrentStartingNode())
        assertEquals(edge1.getPathEnds().second, route.getCurrentEndingNode())
        assertEquals(edge1.distance, route.getCurrentPathDistance())
    }

    @Test
    fun routeCreatedWithMutliplePathReturnsPath() {
        val multiplePathsList = listOf(listOf(edge1, edge2, edge3))

        val route = Route(routeList = multiplePathsList, totalDistance = 0f, sourceNode = 0, numberOfTravellers = 1)

        route.beginJourney()

        assertEquals(listOf(edge1, edge2, edge3), route.getCurrentStage())
        assertEquals(edge1.getPathEnds().first, route.getCurrentStartingNode())
        assertEquals(edge3.getPathEnds().second, route.getCurrentEndingNode())
        assertEquals(multiplePathsList.first().sumOf { it.distance.toDouble() }.toFloat(), route.getCurrentPathDistance())

    }

    @Test
    fun routeCreation_multipleRoutes() {

        val path1 = listOf(edge1, edge2)
        val path2 = listOf(edge3, edge4)
        val multiplePathList = listOf(path1, path2)

        val route = Route(routeList = multiplePathList, totalDistance = 0f, sourceNode = 0, numberOfTravellers = 1)
        route.beginJourney()

        assertEquals(path1, route.getCurrentStage())
        assertEquals(edge1.getPathEnds().first, route.getCurrentStartingNode())
        assertEquals(edge2.getPathEnds().second, route.getCurrentEndingNode())

        route.nextStage()

        assertEquals(path2, route.getCurrentStage())
        assertEquals(edge3.getPathEnds().first, route.getCurrentStartingNode())
        assertEquals(edge4.getPathEnds().second, route.getCurrentEndingNode())

    }
}