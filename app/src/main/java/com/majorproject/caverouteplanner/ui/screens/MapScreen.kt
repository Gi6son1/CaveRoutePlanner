package com.majorproject.caverouteplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
fun DemoMapScreen() {
    BackGroundScaffold {innerPadding ->
        val requester = remember { FocusRequester() }
        var volumeKeyPressed by remember { mutableStateOf(false) }

        var routeFinder by rememberSaveable {
            mutableStateOf(
                RouteFinder(
                    sourceId = 7,
                    survey = llSurvey,
                )
            )
        }

        var nodeNum by rememberSaveable { mutableIntStateOf(0) }

        var currentRoute: Route? by rememberSaveable {
            mutableStateOf(null)
        }

        var noWater by rememberSaveable {
            mutableStateOf(false)
        }

        var highAltitude by rememberSaveable {
            mutableStateOf(false)
        }

        var noHardTraverse by rememberSaveable {
            mutableStateOf(false)
        }

        ConstraintLayout(
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

            val (surveyGraph, demoButtons, flagColumn, cancelButton) = createRefs()

            ImageWithGraphOverlay(
                survey = llSurvey,
                modifier = Modifier
                    .padding(innerPadding)
                    .constrainAs(surveyGraph) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                routeFinder = routeFinder,
                currentRoute = currentRoute,
                longPressPosition = { tapPosition ->
                    val nearestNode = llSurvey.getNearestNode(tapPosition)
                    if (nearestNode != null) {
                        currentRoute = routeFinder.getRouteToNode(nearestNode)
                        nodeNum = nearestNode
                    }
                }

            )

            Button(onClick = {
                currentRoute = null
                nodeNum = 0
            },
                modifier = Modifier.constrainAs(cancelButton) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }
            ) {
                Text(text = "Reset", fontSize = 30.sp)
            }

            Column(modifier = Modifier.constrainAs(flagColumn) {
                bottom.linkTo(demoButtons.top, margin = 10.dp)
                start.linkTo(parent.start, margin = 50.dp)
            }) {
                Row {
                    Switch(
                        checked = noWater,
                        onCheckedChange = {
                            noWater = it
                            routeFinder = RouteFinder(
                                sourceId = 7,
                                survey = llSurvey,
                                flags = Triple(noWater, noHardTraverse, highAltitude)
                            )
                            if (nodeNum != 0) {
                                currentRoute = routeFinder.getRouteToNode(nodeNum)
                            }
                        }

                    )
                    Text(text = "No Water", fontSize = 20.sp)
                }

                Row {
                    Switch(
                        checked = noHardTraverse,
                        onCheckedChange = {
                            noHardTraverse = it
                            routeFinder = RouteFinder(
                                sourceId = 7,
                                survey = llSurvey,
                                flags = Triple(noWater, noHardTraverse, highAltitude)
                            )
                            if (nodeNum != 0) {
                                currentRoute = routeFinder.getRouteToNode(nodeNum)
                            }
                        }
                    )
                    Text(text = "No Hard Traverse", fontSize = 20.sp)
                }

                Row {
                    Switch(
                        checked = highAltitude,
                        onCheckedChange = {
                            highAltitude = it
                            routeFinder = RouteFinder(
                                sourceId = 7,
                                survey = llSurvey,
                                flags = Triple(noWater, noHardTraverse, highAltitude)
                            )
                            if (nodeNum != 0) {
                                currentRoute = routeFinder.getRouteToNode(nodeNum)
                            }
                        }
                    )
                    Text(text = "High Altitude", fontSize = 20.sp)
                }


            }

            Row(modifier = Modifier
                .constrainAs(demoButtons) {
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    currentRoute?.previousStage()
                }
                ) {
                    Text(text = "Previous", fontSize = 30.sp)
                }
                Button(onClick = {
                    currentRoute?.nextStage()
                }
                ) {
                    Text(text = "Next", fontSize = 30.sp)
                }
            }

        }
    }
}

//@Preview
//@Composable
//fun DemoMapScreenPreview() {
//    CaveRoutePlannerTheme {
//        DemoMapScreen()
//    }
//}

@Composable
fun MapScreen() {
    BackGroundScaffold { innerPadding ->
        ConstraintLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {

            var routeFinder: RouteFinder? by rememberSaveable {
                mutableStateOf(null)
            }

            var pinPointNode: Int? by rememberSaveable {
                mutableStateOf(null)
            }




            val (homeButton, surveyGraph) = createRefs()



            ImageWithGraphOverlay(
                survey = llSurvey,
                modifier = Modifier
                    .padding(innerPadding)
                    .constrainAs(surveyGraph) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                routeFinder = routeFinder,
                longPressPosition = { tapPosition ->
                    val nearestNode = llSurvey.getNearestNode(tapPosition)
                    if (nearestNode != null) {
                        pinPointNode = nearestNode
                    }
                },
                pinpointNode = pinPointNode

            )

            FilledIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.constrainAs(homeButton){
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