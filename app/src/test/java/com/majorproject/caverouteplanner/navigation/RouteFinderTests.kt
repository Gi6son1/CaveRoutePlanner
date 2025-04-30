package com.majorproject.caverouteplanner.navigation

import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class RouteFinderTest {

    val nodeA = SurveyNode(x = 0, y = 0, surveyId = -1, id = 0, isEntrance = true)
    val nodeB = SurveyNode(x = 1, y = 1, surveyId = -1, id = 1, isEntrance = true)
    val nodeC = SurveyNode(x = 2, y = 2, surveyId = -1, id = 2)
    val nodeD = SurveyNode(x = 3, y = 3, surveyId = -1, id = 3, isJunction = true)
    val nodeE = SurveyNode(x = 4, y = 4, surveyId = -1, id = 4)

    val pathA = SurveyPath(ends = Pair(0, 1), distance = 4.0f, surveyId = -1, altitude = 1)
    val pathB = SurveyPath(ends = Pair(1, 2), distance = 5.0f, surveyId = -1, altitude = 1)
    val pathC = SurveyPath(ends = Pair(2, 3), distance = 4.0f, surveyId = -1, hasWater = true)
    val pathD = SurveyPath(ends = Pair(3, 4), distance = 4.0f, surveyId = -1, hasWater = true, isHardTraverse = true)
    val pathE = SurveyPath(ends = Pair(4, 0), distance = 4.0f, surveyId = -1, isHardTraverse = true, altitude = -1)

    val testSurvey = Survey(
        nodes = listOf(nodeA, nodeB, nodeC, nodeD, nodeE),
        paths = listOf(pathA, pathB, pathC, pathD, pathE),
        properties = SurveyProperties(
            imageReference = "test",
            pixelsPerMeter = 0f,
            width = 0,
            height = 0,
            northAngle = 0f,
        )
    )

    @Test
    fun routefinderReturnsSimpleRouteTest() {
        val smallSurvey = Survey(
            nodes = listOf(nodeA, nodeB, nodeC),
            paths = listOf(pathA, pathB),
            properties = SurveyProperties(
                imageReference = "test",
                pixelsPerMeter = 0f,
                width = 0,
                height = 0,
                northAngle = 0f,
            )
        )

        val routeFinder = RouteFinder(sourceNode = nodeA, survey = smallSurvey, flags = Triple(false, false, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeC)

        val expectedRouteList = listOf(listOf(pathA, pathB))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
        assertThat(route?.totalDistance, equalTo(9.0f))
    }

    @Test
    fun routefinderReturnsShortestRouteTest() {
        val routeFinder = RouteFinder(sourceNode = nodeA, survey = testSurvey, flags = Triple(false, false, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeD)

        val expectedRouteList = listOf(listOf(pathE, pathD))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
        assertThat(route?.totalDistance, equalTo(8.0f))
    }

    @Test
    fun routefinderReturnsRouteAvoidingWaterTest() {
        val routeFinder = RouteFinder(sourceNode = nodeC, survey = testSurvey, flags = Triple(true, false, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeE)

        val expectedRouteList = listOf(listOf(pathB, pathA, pathE))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
        assertThat(route?.totalDistance, equalTo(13.0f))

    }

    @Test
    fun routefinderReturnsRouteAvoidingHardTraversesTest() {
        val routeFinder = RouteFinder(sourceNode = nodeA, survey = testSurvey, flags = Triple(false, true, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeD)

        val expectedRouteList = listOf(listOf(pathA, pathB, pathC))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
        assertThat(route?.totalDistance, equalTo(13.0f))

    }

    @Test
    fun routefinderReturnsRouteAvoidingHighAltitudeTest() {
        val routeFinder = RouteFinder(sourceNode = nodeA, survey = testSurvey, flags = Triple(false, false, true), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeD)

        val expectedRouteList = listOf(listOf(pathA, pathB, pathC))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
        assertThat(route?.totalDistance, equalTo(13.0f))
    }

    @Test
    fun routefinderReturnsRouteAvoidingAllTest() {
        val routeFinder = RouteFinder(sourceNode = nodeC, survey = testSurvey, flags = Triple(true, true, true), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeE)

        val expectedRouteList = listOf(listOf(pathB, pathA, pathE))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(1))
        assertEquals(expectedRouteList, route?.routeList)
    }

    @Test
    fun routeFinderReturnsRouteWith2Lists(){
        val routeFinder = RouteFinder(sourceNode = nodeC, survey = testSurvey, flags = Triple(false, false, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeE)

        val expectedRouteList = listOf(listOf(pathC), listOf(pathD))

        assertNotNull(route)
        assertThat(route?.routeList?.size, equalTo(2))
        assertEquals(expectedRouteList, route?.routeList)
    }

    @Test
    fun routeFinderReturnsNoRouteIfNoPath(){
        val testSurvey = Survey(
            nodes = listOf(nodeA, nodeB, nodeC, nodeD, nodeE),
            paths = listOf(pathB, pathC, pathE),
            properties = SurveyProperties(
                imageReference = "test",
                pixelsPerMeter = 0f,
                width = 0,
                height = 0,
                northAngle = 0f,
            )
        )

        val routeFinder = RouteFinder(sourceNode = nodeA, survey = testSurvey, flags = Triple(false, false, false), numberOfTravellers = 1)

        val route = routeFinder.getRouteToNode(nodeD)

        assertNull(route)
    }

    @Test
    fun routeFinderFindsNearestExitTest(){
        val routeFinder = RouteFinder(sourceNode = nodeD, survey = testSurvey, flags = Triple(false, false, false), numberOfTravellers = 1)
        val nearestExit = routeFinder.findNearestExit()

        assertNotNull(nearestExit)
        assertEquals(nodeA, nearestExit)
    }
}