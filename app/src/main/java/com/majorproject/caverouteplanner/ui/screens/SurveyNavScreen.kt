package com.majorproject.caverouteplanner.ui.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.datasource.SensorActivity
import com.majorproject.caverouteplanner.model.viewmodel.CaveRoutePlannerViewModel
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.ImageWithGraphOverlay
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.navigationlayouts.InJourneyLayout
import com.majorproject.caverouteplanner.ui.navigationlayouts.PreJourneyLayout
import com.majorproject.caverouteplanner.ui.util.displaySnackbarWithMessage

/**
 * File containing the survey navigation screen composables
 */

/**
 * Top level Composable for the survey navigation screen
 * @param surveyId The id of the survey to be navigated to
 * @param backToMenu A function to return to the menu screen
 * @param surveyViewModel The view model for the survey
 */
@Composable
fun SurveyNavScreenTopLevel(
    surveyId: Int,
    backToMenu: () -> Unit = {},
    surveyViewModel: CaveRoutePlannerViewModel
) {
    val context = LocalContext.current.applicationContext

    var sensorReading: Double? by rememberSaveable { mutableStateOf(null) }

    val surveyList = surveyViewModel.surveyList.collectAsStateWithLifecycle() // List of all surveys as readable objects
    val survey = surveyList.value.find { it.properties.id == surveyId } //finds the survey with the given id and passes it into the navScreen composable

    if (survey != null) {
        SurveyNavScreen(
            survey = survey,
            backToMenu = { backToMenu() },
            sensorReading = sensorReading,
            enableCompass = { //starts the compass sensor when called
                SensorActivity(
                    context = context,
                    sensorReading = {
                        sensorReading = it
                    }
                ).start()
            },
            disableCompass = { //stops the compass sensor when called to save battery
                SensorActivity(
                    context = context,
                    sensorReading = { sensorReading = null }
                ).stop()
            }
        )
    }
}

/**
 * Composable for the survey navigation screen
 * @param survey The survey to be navigated to
 * @param backToMenu A function to return to the menu screen
 * @param enableCompass A function to enable the compass sensor
 * @param disableCompass A function to disable the compass sensor
 * @param sensorReading The current reading of the compass sensor
 */
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

        val context = LocalContext.current

        /**
         * Function to reset the route finder to its original state with the given flags if present, because it's called more than once
         * @param tempFlags The flags to be used in the route finder
         */
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
                .onKeyEvent { keyEvent -> //handles volume up and down key presses that progress the current navigation stages
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
                    if (currentRoute == null || currentRoute?.routeStarted == false) { //if there is no current journey, change the current route to the node specified
                        resetRouteFinder()
                        val nearestNode = survey.nearestNode(tapPosition)

                        if (nearestNode != null) {
                            pinPointNode = nearestNode
                            currentRoute = routeFinder?.getRouteToNode(nearestNode)
                            if (currentRoute == null) {
                                displaySnackbarWithMessage( //display a snackbar message that no route can be found
                                    scope,
                                    snackbarHostState,
                                    context.getString(R.string.no_route_from_source_has_been_found)
                                )
                            }
                        }
                    }
                },
                pinpointDestinationNode = pinPointNode,
                currentRoute = currentRoute,
                pinpointSourceNode = sourceNode,
                onTap = {
                    if (currentRoute != null && currentRoute!!.routeStarted) { //if the screen is tapped, allow the inJourneyExtended view in inJourneyLayout to be true/false
                        inJourneyExtended = !inJourneyExtended
                    }
                },
                compassEnabled = compassEnabled,
                compassReading = sensorReading
            )
        }

        if (currentRoute == null || currentRoute?.routeStarted == false) { //if there is no current journey in play, display the preJourneyLayout
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
                changeConditions = { hasWater, hasHardTraversal, highAltitude, numberOfTravellers -> //if conditions have changed, re-calculate the route with the new conditions
                    currentTravelConditions = Triple(hasWater, hasHardTraversal, highAltitude)
                    currentNumberOfTravellers = numberOfTravellers
                    resetRouteFinder()
                    if (pinPointNode != null) {
                        currentRoute = routeFinder?.getRouteToNode(pinPointNode!!)
                        if (currentRoute == null) {
                            displaySnackbarWithMessage(
                                scope,
                                snackbarHostState,
                                context.getString(R.string.no_route_from_source_has_been_found)
                            )
                            pinPointNode = null
                        }
                    }
                },
                caveExit = { //if cave exit is selected, find the nearest exit and set it as the destination
                    if (pinPointNode != null) {
                        sourceNode = pinPointNode!!

                        resetRouteFinder()

                        val nearestExit = routeFinder?.findNearestExit()
                        pinPointNode = nearestExit
                        if (nearestExit != null) currentRoute =
                            routeFinder?.getRouteToNode(nearestExit)
                        if (currentRoute == null) {
                            displaySnackbarWithMessage( //display a snackbar message that no route can be found
                                scope,
                                snackbarHostState,
                                context.getString(R.string.no_route_from_source_has_been_found)
                            )
                            pinPointNode = null
                        }
                    }
                },
                currentTravelConditions = currentTravelConditions,
                numberOfTravellers = currentNumberOfTravellers,
                returnToMenu = { backToMenu() },
                displayCaveExitButton = !survey.caveExits().contains(pinPointNode) //if the current source node is not a cave exit, display the cave exit button
            )
        } else if (currentRoute != null) { //if there is a current journey in play, display the inJourneyLayout
            InJourneyLayout(
                currentRoute = currentRoute!!,
                cancelRoute = {
                    currentRoute = null
                    pinPointNode = null
                },
                caveExit = { currentLocation, emergencyExit -> //if cave exit is selected, find the nearest exit and set it as the destination
                    val foundNode = survey.nodes.find { it.getNodeId() == currentLocation }
                    if (foundNode != null) {
                        sourceNode = foundNode
                        resetRouteFinder(if (emergencyExit) Triple(false, false, true) else null) //if emergency exit is selected, set the high altitude flag to true when resetting the route finder

                        val nearestExit = routeFinder?.findNearestExit()

                        if (nearestExit != null) {
                            pinPointNode = nearestExit

                            currentRoute = routeFinder?.getRouteToNode(nearestExit)

                            if (currentRoute == null) {
                                displaySnackbarWithMessage(
                                    scope,
                                    snackbarHostState,
                                    context.getString(R.string.no_route_from_source_has_been_found)
                                )
                                pinPointNode = null
                            }
                        }
                    }

                },
                extendedView = inJourneyExtended,
                onCompassClick = { //if compass is clicked, toggle the compass enabled flag to enable/disable the compass sensor
                    compassEnabled = !compassEnabled
                    if (compassEnabled) enableCompass() else disableCompass()
                },
                compassEnabled = compassEnabled
            )
        }
    }
}