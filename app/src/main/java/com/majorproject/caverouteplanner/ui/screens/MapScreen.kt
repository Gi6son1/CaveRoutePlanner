package com.majorproject.caverouteplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.components.ImageWithGraphOverlay
import com.majorproject.caverouteplanner.ui.components.llSurvey
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.navigationlayouts.InJourneyLayout
import com.majorproject.caverouteplanner.ui.navigationlayouts.PreJourneyLayout
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

@Composable
fun MapScreen() {
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
            mutableIntStateOf(7)
        }

        LaunchedEffect(sourceId) {
            routeFinder = RouteFinder(
                sourceId = sourceId,
                survey = llSurvey,
            )
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
                survey = llSurvey,
                modifier = Modifier
                    .fillMaxSize(),
                longPressPosition = { tapPosition ->
                    routeFinder = RouteFinder(
                        sourceId = sourceId,
                        survey = llSurvey,
                    )
                    val nearestNode = llSurvey.getNearestNode(tapPosition)

                    if (nearestNode != null) {
                        pinPointNode = nearestNode
                        currentRoute = routeFinder?.getRouteToNode(nearestNode)
                    }
                },
                pinpointDestinationNode = pinPointNode,
                currentRoute = currentRoute,
                pinpointSourceNode = sourceId
            )
        }

        if (currentRoute == null || currentRoute?.routeStarted == false) {
            PreJourneyLayout(
                currentRoute = currentRoute,
                setSource = {
                    sourceId = pinPointNode!!
                    pinPointNode = null
                    currentRoute = null
                    Log.d("pinpoint", sourceId.toString())
                },
                removePin = {
                    pinPointNode = null
                    currentRoute = null
                },
                changeConditions = { hasWater, hasHardTraversal, highAltitude ->

                },
                caveExit = {
                    sourceId = pinPointNode!!

                    routeFinder = RouteFinder(
                        sourceId = sourceId,
                        survey = llSurvey,
                    )

                    val nearestExit = routeFinder?.findNearestExit()
                    pinPointNode = nearestExit
                    if (nearestExit != null) currentRoute = routeFinder?.getRouteToNode(nearestExit)
                }
            )
        } else if (currentRoute != null){
            InJourneyLayout(
                currentRoute = currentRoute!!,
                cancelRoute = {
                    currentRoute = null
                    pinPointNode = null
                },
                caveExit = { currentLocation ->
                    routeFinder = RouteFinder(
                        sourceId = currentLocation,
                        survey = llSurvey,
                    )

                    val nearestExit = routeFinder?.findNearestExit()

                    if (nearestExit != null) {
                        pinPointNode = nearestExit
                        currentRoute = routeFinder?.getRouteToNode(nearestExit)
                    }
                },
            )
        }
    }
}



@Preview
@Composable
fun MapScreenPreview(){
    CaveRoutePlannerTheme {
        MapScreen()
    }
}