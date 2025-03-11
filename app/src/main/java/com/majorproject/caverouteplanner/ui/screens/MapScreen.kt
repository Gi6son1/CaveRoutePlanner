package com.majorproject.caverouteplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            val (surveyGraph, demoButtons) = createRefs()

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

            var newRoute by rememberSaveable {
                mutableStateOf(true)
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
                        routeFinder = RouteFinder(
                            sourceId = 7,
                            survey = llSurvey,
                        )
                        newRoute = true
                    } else if (newRoute) {
                        currentRoute = routeFinder.getRouteToNode(37)
                        newRoute = false
                    } else {
                        currentRoute?.nextStage()
                    }

                }) {
                    Text(text = "Shortest Dist", fontSize = 30.sp)
                }
                Button(onClick = {
                    if (demoNum != 2) {
                        demoNum = 2
                        routeFinder = RouteFinder(
                            sourceId = 7,
                            survey = llSurvey,
                            flags = Triple(true, false, false)
                        )
                        newRoute = true
                    } else if (newRoute) {
                        currentRoute = routeFinder.getRouteToNode(37)
                        newRoute = false
                    } else {
                        currentRoute?.nextStage()
                    }
                }) {
                    Text(text = "No water", fontSize = 30.sp)
                }
            }
        }
    }
}