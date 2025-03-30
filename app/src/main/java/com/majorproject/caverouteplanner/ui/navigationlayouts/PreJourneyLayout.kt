package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import com.majorproject.caverouteplanner.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTripInfoBox

@Composable
fun PreJourneyLayout(
    currentRoute: Route?,
    setSource: () -> Unit = {},
    removePin: () -> Unit = {},
    changeConditions: (Boolean, Boolean, Boolean) -> Unit = {_, _, _ ->},
    caveExit: () -> Unit = {}
){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (homeButton, goButton, setSource, cancel , changeConditions, caveExit) = createRefs()


        CustomIconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.constrainAs(homeButton) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 10.dp)
            },
            iconVector = Icons.Outlined.Home,
            contentDescription = "Home"
        )

        CustomTextButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.constrainAs(changeConditions) {
                top.linkTo(homeButton.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 10.dp)
            },
            text = "Travel Conditions",
            iconVector = Icons.Outlined.Edit,
            contentDescription = "Edit"
        )


        if (currentRoute != null && currentRoute.routeStarted == false) {
            CustomTripInfoBox(
                onClick = { currentRoute.beginJourney() },
                isGoButton = true,
                modifier = Modifier.constrainAs(goButton) {
                    bottom.linkTo(setSource.top, 30.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                },
                distance = currentRoute.totalDistance,
                time = currentRoute.getTotalPathTravelTime()
            )

            CustomTextButton(
                onClick = { setSource() },
                modifier = Modifier.constrainAs(setSource) {
                    bottom.linkTo(parent.bottom, 40.dp)
                    start.linkTo(cancel.end)
                    end.linkTo(parent.end, 10.dp)
                },
                text = "Set As Source",
                iconVector = Icons.Outlined.LocationOn,
                contentDescription = "Set as source"
            )

            CustomTextButton(
                onClick = { removePin() },
                modifier = Modifier.constrainAs(cancel){
                    bottom.linkTo(parent.bottom, 40.dp)
                    end.linkTo(setSource.start, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                },
                text = "Remove Flag Pin",
                iconVector = Icons.Outlined.Flag,
                contentDescription = "Remove Pin"
            )

            CustomTextButton(
                onClick = { caveExit() },
                modifier = Modifier.constrainAs(caveExit){
                    top.linkTo(changeConditions.bottom, 20.dp)
                    end.linkTo(parent.end, 10.dp)
                },
                text = "Exit Cave From Flag",
                iconImage = R.drawable.exit_icon,
                contentDescription = "Exit Cave From Flag"
            )
        }
    }
}
