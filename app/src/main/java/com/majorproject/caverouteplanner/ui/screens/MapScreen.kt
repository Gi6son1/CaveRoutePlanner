package com.majorproject.caverouteplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.RouteFinder
import com.majorproject.caverouteplanner.ui.components.ImageWithGraphOverlay
import com.majorproject.caverouteplanner.ui.components.llSurvey
import androidx.compose.runtime.getValue
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
                routeFinder = routeFinder,
                longPressPosition = { tapPosition ->
                    routeFinder = RouteFinder(
                        sourceId = 7,
                        survey = llSurvey,
                    )
                    val nearestNode = llSurvey.getNearestNode(tapPosition)

                    if (nearestNode != null) {
                        pinPointNode = nearestNode
                        currentRoute = routeFinder?.getRouteToNode(nearestNode)
                    }
                },
                pinpointNode = pinPointNode,
                currentRoute = currentRoute
            )
        }

        if (currentRoute == null || currentRoute?.routeStarted == false) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                val (homeButton, goButton) = createRefs()

                FilledIconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(homeButton) {
                        top.linkTo(parent.top, margin = 20.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                    }
                        .size(60.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                if (currentRoute != null && currentRoute?.routeStarted == false) {
                    Button(
                        onClick = {
                            currentRoute?.beginJourney()
                        },
                        modifier = Modifier.constrainAs(goButton) {
                            bottom.linkTo(parent.bottom, 30.dp)
                            start.linkTo(parent.start, 20.dp)
                            end.linkTo(parent.end, 20.dp)
                        }
                    ) {
                        Text(text = "Go", fontSize = 30.sp)
                    }
                }
            }
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