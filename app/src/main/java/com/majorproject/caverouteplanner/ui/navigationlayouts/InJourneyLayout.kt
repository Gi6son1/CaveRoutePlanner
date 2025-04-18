package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.components.customcomposables.ActionCheckDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CaveExitDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTripInfoBox

@Composable
fun InJourneyLayout(
    currentRoute: Route,
    cancelRoute: () -> Unit = {},
    caveExit: (Int, Boolean) -> Unit = { _, _ -> },
    extendedView: Boolean,
    onCompassClick: () -> Unit = {},
    compassEnabled: Boolean
) {
    var openCancelRouteCheckDialog by rememberSaveable { mutableStateOf(false) }
    var openCaveExitDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler { openCancelRouteCheckDialog = true }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (routeDetails,
            pathDetails,
            cancel,
            caveExit,
            compass,
            nextStage,
            prevStage) = createRefs()

        CustomTripInfoBox(
            modifier = Modifier.constrainAs(routeDetails) {
                top.linkTo(parent.top, 40.dp)
                start.linkTo(parent.start, 20.dp)
            },
            distance = currentRoute.totalDistance,
            time = currentRoute.getTotalPathTravelTime()
        )

        CustomTextButton(
            onClick = { currentRoute.nextStage() },
            text = "Next Stage",
            modifier = Modifier.constrainAs(nextStage) {
                bottom.linkTo(parent.bottom, 40.dp)
                end.linkTo(parent.end, 10.dp)
            },
            iconVector = Icons.AutoMirrored.Outlined.ArrowForward
        )

        CustomTextButton(
            onClick = { currentRoute.previousStage() },
            text = "Previous Stage",
            modifier = Modifier.constrainAs(prevStage) {
                bottom.linkTo(parent.bottom, 40.dp)
                start.linkTo(parent.start, 10.dp)
            },
            iconVector = Icons.AutoMirrored.Outlined.ArrowBack,
            flipped = true
        )

        CustomIconButton(
            onClick = { openCaveExitDialog = true },
            modifier = Modifier.constrainAs(caveExit) {
                top.linkTo(parent.top, 40.dp)
                end.linkTo(parent.end, 20.dp)
            },
            iconImage = R.drawable.exit_icon,
            invertedColour = true
        )

        CustomIconButton(
            onClick = { onCompassClick() },
            modifier = Modifier.constrainAs(compass) {
                bottom.linkTo(nextStage.top, 60.dp)
                end.linkTo(parent.end, 20.dp)
            },
            iconImage = R.drawable.compass_icon,
            invertedColour = !compassEnabled
        )


        if (extendedView){
            CustomTripInfoBox(
                modifier = Modifier.constrainAs(pathDetails) {
                    top.linkTo(routeDetails.bottom, 20.dp)
                    start.linkTo(parent.start, 20.dp)
                },
                pathNotDest = true,
                distance = currentRoute.getCurrentPathDistance(),
                time = currentRoute.getCurrentPathTravelTime()
            )

            CustomIconButton(
                onClick = { openCancelRouteCheckDialog = true },
                modifier = Modifier.constrainAs(cancel) {
                    top.linkTo(caveExit.bottom, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                },
                iconVector = Icons.Outlined.Cancel,
                contentDescription = "Cancel"
            )
        }

        ActionCheckDialog(
            dialogIsOpen = openCancelRouteCheckDialog,
            dialogOpen = { openCancelRouteCheckDialog = it },
            confirmAction = { cancelRoute() },
            message = "Are you sure you want to remove this route? You'll lose your progress if you do."
        )

        CaveExitDialog(
            dialogIsOpen = openCaveExitDialog,
            dialogOpen = { openCaveExitDialog = it },
            highAltitudeExit = { caveExit(currentRoute.getCurrentEndingNode(), true) },
            normalExit = { caveExit(currentRoute.getCurrentEndingNode(), false) }
        )
    }

}