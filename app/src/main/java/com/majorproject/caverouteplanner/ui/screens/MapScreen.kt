package com.majorproject.caverouteplanner.ui.screens

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

            var demoNum by remember { mutableIntStateOf(0) }

            ImageWithGraphOverlay(
                survey = llSurvey,
                modifier = Modifier
                    .padding(innerPadding)
                    .constrainAs(surveyGraph) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                routeFinder = routeFinder,
                currentRoute = if (demoNum != 0) routeFinder.currentRoute else null
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
                    demoNum = 1
                    routeFinder = RouteFinder(
                        sourceId = 7,
                        survey = llSurvey,
                    )
                    routeFinder.setRouteToNode(37)
                }) {
                    Text(text = "Demo 1", fontSize = 30.sp)
                }
                Button(onClick = {
                    demoNum = 2
                    routeFinder = RouteFinder(
                        sourceId = 7,
                        survey = llSurvey,
                        flags = Triple(true, false, false)
                    )
                    routeFinder.setRouteToNode(37)
                }) {
                    Text(text = "Demo 2", fontSize = 30.sp)
                }
            }
        }
    }
}