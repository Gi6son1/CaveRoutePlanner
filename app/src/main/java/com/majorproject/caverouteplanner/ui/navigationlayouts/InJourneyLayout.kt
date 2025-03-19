package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.Route
import androidx.compose.material3.Button

@Composable
fun InJourneyLayout(
    currentRoute: Route,
    cancelRoute: () -> Unit = {},
    caveExit: (Int) -> Unit = {}
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (routeDetails,
            pathDetails,
            cancel,
            caveExit,
            nextStage,
            prevStage) = createRefs()

        Row(modifier = Modifier.constrainAs(routeDetails) {
            top.linkTo(parent.top, 20.dp)
            start.linkTo(parent.start, 20.dp)
        }
        )
        {
            Text(text = currentRoute.totalDistance.toString())
        }

        Row(modifier = Modifier.constrainAs(pathDetails) {
            top.linkTo(routeDetails.bottom, 20.dp)
            start.linkTo(parent.start, 20.dp)
        }
        ) {
            Text(text = currentRoute.getCurrentPathDistance().toString())
        }

        Button(onClick = { currentRoute.nextStage() }, modifier = Modifier.constrainAs(nextStage){
            bottom.linkTo(parent.bottom, 20.dp)
            start.linkTo(parent.start, 20.dp)
        }) {
            Text(text = "Next Stage")
        }

        Button(onClick = { currentRoute.previousStage() }, modifier = Modifier.constrainAs(prevStage){
            bottom.linkTo(parent.bottom, 20.dp)
            end.linkTo(parent.end, 20.dp)
        }) {
            Text(text = "Previous Stage")
        }

        Button(onClick = { cancelRoute() }, modifier = Modifier.constrainAs(cancel){
            top.linkTo(caveExit.bottom, 20.dp)
            end.linkTo(parent.end, 20.dp)
        } )
        {
            Text(text = "Cancel")
        }

        Button(onClick = {
            //val
            caveExit(currentRoute.getCurrentEndingNode())
                         }
            , modifier = Modifier.constrainAs(caveExit){
            top.linkTo(routeDetails.bottom, 20.dp)
            end.linkTo(parent.end, 20.dp)
        }) {
            Text(text = "Cave Exit")
        }
    }

}