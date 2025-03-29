package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.majorproject.caverouteplanner.navigation.Route
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomIconButton
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomTextButton

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
            icon = Icons.Outlined.Home,
            contentDescription = "Home"
        )

        CustomTextButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.constrainAs(changeConditions) {
                top.linkTo(homeButton.bottom, margin = 20.dp)
                end.linkTo(parent.end, margin = 10.dp)
            },
            text = "Travel Conditions",
            icon = Icons.Outlined.Edit,
            contentDescription = "Edit"
        )


        if (currentRoute != null && currentRoute.routeStarted == false) {
            Button(
                onClick = {
                    currentRoute.beginJourney()
                },
                modifier = Modifier.constrainAs(goButton) {
                    bottom.linkTo(setSource.top, 30.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                }
            ) {
                Text(text = "Go", fontSize = 30.sp)
            }

            CustomTextButton(
                onClick = { setSource() },
                modifier = Modifier.constrainAs(setSource) {
                    bottom.linkTo(parent.bottom, 20.dp)
                    start.linkTo(cancel.end)
                    end.linkTo(parent.end, 10.dp)
                },
                text = "Set As Source",
                icon = Icons.Outlined.Edit,
                contentDescription = "Edit"
            )

            Button(
                onClick = { removePin() },
                modifier = Modifier.constrainAs(cancel){
                    bottom.linkTo(parent.bottom, 20.dp)
                    end.linkTo(setSource.start, 10.dp)
                    start.linkTo(parent.start, 10.dp)
                }
            ) {
                Text(text = "Remove Pin", fontSize = 20.sp)
            }

            Button(
                onClick = { caveExit() },
                modifier = Modifier.constrainAs(caveExit){
                    top.linkTo(changeConditions.bottom, 20.dp)
                    end.linkTo(parent.end, 10.dp)
                }
            ) {
                Text(text = "Cave Exit From Destination", fontSize = 20.sp)
            }


        }
    }
}
