package com.majorproject.caverouteplanner.ui.screens

import android.app.Application
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.majorproject.caverouteplanner.datasource.SensorActivity
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.ImageWithGraphOverlay
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.navigationlayouts.InJourneyLayout
import com.majorproject.caverouteplanner.ui.navigationlayouts.PreJourneyLayout
import kotlinx.coroutines.launch

@Composable
fun SurveyNavScreenTopLevel(
    surveyId: Int,
    backToMenu: () -> Unit = {},
){
    val context = LocalContext.current.applicationContext

    var sensorReading: Double? by rememberSaveable { mutableStateOf(null) }

    val repository = CaveRoutePlannerRepository(context as Application)

    val survey = repository.getSurveyWithDataById(surveyId)

    if (survey != null){
        SurveyNavScreen(
            survey = survey,
            backToMenu = { backToMenu() },
            sensorReading = sensorReading,
            enableCompass = {
                SensorActivity(
                    context = context,
                    sensorReading = {
                        sensorReading = it
                    }
                ).start()
            },
            disableCompass = {
                SensorActivity(
                    context = context,
                    sensorReading = { sensorReading = null }
                ).stop()
            }
        )
    }
}

@Composable
fun SurveyNavScreen(
    survey: Survey,
    backToMenu: () -> Unit = {},
    enableCompass: () -> Unit = {},
    disableCompass: () -> Unit = {},
    sensorReading: Double?
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    BackGroundScaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) { innerPadding ->
        val requester = remember { FocusRequester() }
        var volumeKeyPressed by remember { mutableStateOf(false) }

        var routeFinder: RouteFinder? by rememberSaveable {
            mutableStateOf(null)
        }

        var pinPointNode: SurveyNode? by rememberSaveable {
            mutableStateOf(null)
        }

        var currentRoute: Route? by rememberSaveable {
            mutableStateOf(null)
        }

        var sourceNode: SurveyNode by rememberSaveable {
            mutableStateOf(survey.nodes[0])
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
                sourceNode = sourceNode,
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
                        val nearestNode = survey.nearestNode(tapPosition)

                        if (nearestNode != null) {
                            pinPointNode = nearestNode
                            currentRoute = routeFinder?.getRouteToNode(nearestNode)
                            if (currentRoute == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(message = "No route from source has been found")
                                }
                                pinPointNode = null
                            }
                        }
                    }
                },
                pinpointDestinationNode = pinPointNode,
                currentRoute = currentRoute,
                pinpointSourceNode = sourceNode,
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
                    if (pinPointNode != null) {
                        sourceNode = pinPointNode!!
                        pinPointNode = null
                        currentRoute = null
                    }
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
                        if (currentRoute == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "No route from source has been found")
                            }
                            pinPointNode = null
                        }
                    }
                },
                caveExit = {
                    if (pinPointNode != null){
                        sourceNode = pinPointNode!!

                        resetRouteFinder()

                        val nearestExit = routeFinder?.findNearestExit()
                        pinPointNode = nearestExit
                        if (nearestExit != null) currentRoute = routeFinder?.getRouteToNode(nearestExit)
                        if (currentRoute == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "No route from source has been found")
                            }
                            pinPointNode = null
                        }
                    }
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
                    val foundNode = survey.nodes.find { it.getNodeId() == currentLocation }
                    if (foundNode != null) {
                        sourceNode = foundNode
                        resetRouteFinder(if (emergencyExit) Triple(false, false, true) else null)

                        val nearestExit = routeFinder?.findNearestExit()

                        if (nearestExit != null) {
                            pinPointNode = nearestExit

                            currentRoute = routeFinder?.getRouteToNode(nearestExit)

                            if (currentRoute == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(message = "No route from source has been found")
                                }
                                pinPointNode = null
                            }
                        }
                    }

                },
                extendedView = inJourneyExtended,
                onCompassClick = {
                    compassEnabled = !compassEnabled
                    if (compassEnabled) enableCompass() else disableCompass()
                },
                compassEnabled = compassEnabled
            )
        }
    }
}
