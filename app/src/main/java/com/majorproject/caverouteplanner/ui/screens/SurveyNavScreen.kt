package com.majorproject.caverouteplanner.ui.screens

import android.app.Application
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.ImageWithGraphOverlay
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.navigationlayouts.InJourneyLayout
import com.majorproject.caverouteplanner.ui.navigationlayouts.PreJourneyLayout

@Composable
fun SurveyNavScreenTopLevel(
    surveyId: Int,
    backToMenu: () -> Unit = {},
    sensorReading: Double?
){
    val context = LocalContext.current.applicationContext
    val repository = CaveRoutePlannerRepository(context as Application)

    val survey = repository.getSurveyWithDataById(surveyId)

    if (survey != null){
        SurveyNavScreen(
            survey = survey,
            backToMenu = { backToMenu() },
            sensorReading = sensorReading
        )
    }
}

@Composable
fun SurveyNavScreen(
    survey: Survey,
    backToMenu: () -> Unit = {},
    sensorReading: Double? = null
) {
    BackGroundScaffold { innerPadding ->
        val requester = remember { FocusRequester() }
        var volumeKeyPressed by remember { mutableStateOf(false) }

        var routeFinder: RouteFinder? by rememberSaveable {
            mutableStateOf(null)
        }

        var pinPointNode: Int? by rememberSaveable {
            mutableStateOf(null)
        }

        var currentRoute: Route? by rememberSaveable {
            mutableStateOf(null)
        }

        var sourceId: Int by rememberSaveable {
            mutableIntStateOf(0)
        }

        var compassEnabled: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        var currentTravelConditions: Triple<Boolean, Boolean, Boolean> by rememberSaveable {
            mutableStateOf(Triple(false, false, false))
        }

        var currentNumberOfTravellers: Int by rememberSaveable {
            mutableIntStateOf(1)
        }

        var inJourneyExtended: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        fun resetRouteFinder(tempFlags: Triple<Boolean, Boolean, Boolean>? = null) {
            routeFinder = RouteFinder(
                sourceId = sourceId,
                survey = survey,
                flags = if (tempFlags != null) tempFlags else currentTravelConditions,
                numberOfTravellers = currentNumberOfTravellers
            )
            inJourneyExtended = false
        }


        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .focusRequester(requester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    when {
                        keyEvent.key == Key.VolumeUp && keyEvent.type == KeyEventType.KeyDown && !volumeKeyPressed -> {
                            currentRoute?.nextStage()
                            volumeKeyPressed = true
                            true
                        }

                        keyEvent.key == Key.VolumeDown && keyEvent.type == KeyEventType.KeyDown && !volumeKeyPressed -> {
                            currentRoute?.previousStage()
                            volumeKeyPressed = true
                            true
                        }

                        (keyEvent.key == Key.VolumeUp || keyEvent.key == Key.VolumeDown) && keyEvent.type == KeyEventType.KeyUp -> {
                            volumeKeyPressed = false
                            false
                        }

                        else -> false
                    }
                }
        ) {
            LaunchedEffect(Unit) {
                requester.requestFocus()
            }

            ImageWithGraphOverlay(
                survey = survey,
                modifier = Modifier
                    .fillMaxSize(),
                longPressPosition = { tapPosition ->
                    if (currentRoute == null || currentRoute?.routeStarted == false) {
                        resetRouteFinder()
                        val nearestNode = survey.getNearestNode(tapPosition)

                        if (nearestNode != null) {
                            pinPointNode = nearestNode
                            currentRoute = routeFinder?.getRouteToNode(nearestNode)
                        }
                    }
                },
                pinpointDestinationNode = pinPointNode,
                currentRoute = currentRoute,
                pinpointSourceNode = sourceId,
                onTap = {
                    if (currentRoute != null && currentRoute!!.routeStarted){
                        inJourneyExtended = !inJourneyExtended
                    }
                },
                compassEnabled = compassEnabled,
                compassReading = sensorReading
            )
        }

        if (currentRoute == null || currentRoute?.routeStarted == false) {
            PreJourneyLayout(
                currentRoute = currentRoute,
                setSource = {
                    sourceId = pinPointNode!!
                    pinPointNode = null
                    currentRoute = null
                },
                removePin = {
                    pinPointNode = null
                    currentRoute = null
                },
                changeConditions = { hasWater, hasHardTraversal, highAltitude, numberOfTravellers ->
                    currentTravelConditions = Triple(hasWater, hasHardTraversal, highAltitude)
                    currentNumberOfTravellers = numberOfTravellers
                    resetRouteFinder()
                    if (pinPointNode != null) {
                        currentRoute = routeFinder?.getRouteToNode(pinPointNode!!)
                    }
                },
                caveExit = {
                    sourceId = pinPointNode!!

                    resetRouteFinder()

                    val nearestExit = routeFinder?.findNearestExit()
                    pinPointNode = nearestExit
                    if (nearestExit != null) currentRoute = routeFinder?.getRouteToNode(nearestExit)
                },
                currentTravelConditions = currentTravelConditions,
                numberOfTravellers = currentNumberOfTravellers,
                returnToMenu = { backToMenu() }
            )
        } else if (currentRoute != null) {
            InJourneyLayout(
                currentRoute = currentRoute!!,
                cancelRoute = {
                    currentRoute = null
                    pinPointNode = null
                },
                caveExit = { currentLocation, emergencyExit ->
                    sourceId = currentLocation
                    resetRouteFinder(if (emergencyExit) Triple(false, false, true) else null)

                    val nearestExit = routeFinder?.findNearestExit()

                    if (nearestExit != null) {
                        pinPointNode = nearestExit

                        currentRoute = routeFinder?.getRouteToNode(nearestExit)
                    }
                },
                extendedView = inJourneyExtended,
                onCompassClick = {
                    compassEnabled = !compassEnabled
                },
                compassEnabled = compassEnabled
            )
        }
    }
}
