package com.majorproject.caverouteplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.majorproject.caverouteplanner.navigation.Route

@Composable
fun MapScreen() {
    Scaffold { innerPadding ->

        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val (surveyGraph, demoButtons, flagColumn) = createRefs()

            var routeFinder by rememberSaveable {
                mutableStateOf(
                    RouteFinder(
                        sourceId = 7,
                        survey = llSurvey,
                    )
                )
            }

            var demoNum by rememberSaveable { mutableIntStateOf(0) }

            var currentRoute: Route? by rememberSaveable {
                mutableStateOf(null)
            }

            var onlyFirstClick by rememberSaveable {
                mutableStateOf(true)
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

            ImageWithGraphOverlay(
                survey = llSurvey,
                modifier = Modifier
                    .padding(innerPadding)
                    .constrainAs(surveyGraph) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                routeFinder = routeFinder,
                currentRoute = currentRoute
            )
            Column(modifier = Modifier.constrainAs(flagColumn){
                bottom.linkTo(demoButtons.top, margin = 10.dp)
                start.linkTo(parent.start, margin = 10.dp)
                end.linkTo(parent.end, margin = 10.dp)
            }){
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
                            if (demoNum != 0) {
                                val routeNum = if (demoNum == 2) 55 else 37
                                currentRoute = routeFinder.getRouteToNode(routeNum)
                                onlyFirstClick = false
                            }
                        }

                    )
                    Text(text = "No Water", fontSize = 20.sp)
                }

                Row{
                    Switch(
                        checked = noHardTraverse,
                        onCheckedChange = {
                            noHardTraverse = it
                            routeFinder = RouteFinder(
                                sourceId = 7,
                                survey = llSurvey,
                                flags = Triple(noWater, noHardTraverse, highAltitude)
                            )
                            if (demoNum != 0) {
                                val routeNum = if (demoNum == 2) 55 else 37
                                currentRoute = routeFinder.getRouteToNode(routeNum)
                                onlyFirstClick = false
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
                            if (demoNum != 0){
                                val routeNum = if (demoNum == 2) 55 else 37
                                currentRoute = routeFinder.getRouteToNode(routeNum)
                                onlyFirstClick = false
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
                    if (demoNum != 1){
                        demoNum = 1
                        currentRoute = routeFinder.getRouteToNode(37)
                    } else {
                        currentRoute?.nextStage()
                    }
                }) {
                    Text(text = "Demo 1", fontSize = 30.sp)
                }
                Button(onClick = {
                    if (demoNum != 2){
                        demoNum = 2
                        currentRoute = routeFinder.getRouteToNode(55)
                    } else {
                        currentRoute?.nextStage()
                    }
                }) {
                    Text(text = "Demo 2", fontSize = 30.sp)
                }
            }
        }
    }
}