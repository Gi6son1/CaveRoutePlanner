package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.components.customcomposables.ActionCheckDialog
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTripInfoBox
import com.majorproject.caverouteplanner.ui.components.customcomposables.HelpMessageBox
import com.majorproject.caverouteplanner.ui.components.customcomposables.TravelConditionsDialog

/**
 * Composable to hold the preJourneyLayout - the layout the user will see if they are not currently in a journey
 * @param currentRoute the current route
 * @param returnToMenu function to call when the return to menu button is pressed
 * @param setSource function to call when the set source button is pressed
 * @param removePin function to call when the remove pin button is pressed
 * @param changeConditions function to call when the change conditions button is pressed
 * @param caveExit function to call when the cave exit button is pressed
 * @param currentTravelConditions the current travel conditions
 * @param numberOfTravellers the number of travellers
 * @param displayCaveExitButton whether the cave exit button should be displayed
 */
@Composable
fun PreJourneyLayout(
    currentRoute: Route?,
    returnToMenu: () -> Unit = {},
    setSource: () -> Unit = {},
    removePin: () -> Unit = {},
    changeConditions: (Boolean, Boolean, Boolean, Int) -> Unit = { _, _, _, _ -> },
    caveExit: () -> Unit = {},
    currentTravelConditions: Triple<Boolean, Boolean, Boolean>,
    numberOfTravellers: Int,
    displayCaveExitButton: Boolean
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (homeButton, goButton, setSource, cancel, changeConditions, caveExit) = createRefs()

        var openConditionsDialog by rememberSaveable {
            mutableStateOf(false)
        }

        var openHomeButtonDialog by rememberSaveable {
            mutableStateOf(false)
        }

        BackHandler { openHomeButtonDialog = true } //if the back button is pressed, show the home button dialog


        CustomIconButton(
            onClick = { openHomeButtonDialog = true },
            modifier = Modifier.constrainAs(homeButton) {
                top.linkTo(parent.top, margin = 40.dp)
                start.linkTo(parent.start, margin = 10.dp)
            },
            iconVector = Icons.Outlined.Home,
            contentDescription = "Home"
        )

        CustomTextButton(
            onClick = { openConditionsDialog = true },
            modifier = Modifier.constrainAs(changeConditions) {
                top.linkTo(homeButton.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 10.dp)
            },
            text = "Travel Conditions",
            iconVector = Icons.Outlined.Edit,
            contentDescription = "Edit"
        )


        if (currentRoute != null && currentRoute.routeStarted == false) { //if the route has not been started, show the below buttons
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
                text = stringResource(R.string.set_as_source),
                iconVector = Icons.Outlined.LocationOn,
                contentDescription = stringResource(R.string.set_as_source)
            )

            CustomTextButton(
                onClick = { removePin() },
                modifier = Modifier.constrainAs(cancel) {
                    bottom.linkTo(parent.bottom, 40.dp)
                    end.linkTo(setSource.start, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                },
                text = stringResource(R.string.remove_flag_pin),
                iconVector = Icons.Outlined.Flag,
                contentDescription = stringResource(R.string.remove_flag_pin)
            )

            if (displayCaveExitButton) { //if the cave exit button should be displayed, show it
                CustomTextButton(
                    onClick = { caveExit() },
                    modifier = Modifier.constrainAs(caveExit) {
                        top.linkTo(changeConditions.bottom, 20.dp)
                        end.linkTo(parent.end, 10.dp)
                    },
                    text = stringResource(R.string.exit_cave_from_flag),
                    iconImage = R.drawable.exit_icon,
                    contentDescription = stringResource(R.string.exit_cave_from_flag)
                )
            }
        } else { //if there is no current route, show the help box
            HelpMessageBox(
                modifier = Modifier.constrainAs(goButton) {
                    bottom.linkTo(parent.bottom, 80.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                },
                message = stringResource(R.string.long_press_a_point_on_the_survey_to_select_a_location),
                boxHeight = 68.dp
            )
        }


        TravelConditionsDialog(
            dialogIsOpen = openConditionsDialog,
            dialogOpen = { openConditionsDialog = it },
            currentConditions = currentTravelConditions,
            currentNumberOfTravellers = numberOfTravellers,
            updatedConditions = { noWater, noHardTraverse, highAltitude, numberOfTravellers ->
                changeConditions(noWater, noHardTraverse, highAltitude, numberOfTravellers)
            }
        )

        ActionCheckDialog(
            dialogIsOpen = openHomeButtonDialog,
            dialogOpen = { openHomeButtonDialog = it },
            confirmAction = { returnToMenu() },
            message = stringResource(R.string.are_you_sure_you_d_like_to_go_back_to_the_main_menu_you_will_lose_your_current_route_setup_if_you_do)
        )
    }
}
