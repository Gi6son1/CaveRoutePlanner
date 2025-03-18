package com.majorproject.caverouteplanner.ui.navigationlayouts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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

@Composable
fun PreJourneyLayout(
    currentRoute: Route?
){
    ConstraintLayout(
        modifier = Modifier
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

        if (currentRoute != null && currentRoute.routeStarted == false) {
            Button(
                onClick = {
                    currentRoute.beginJourney()
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
