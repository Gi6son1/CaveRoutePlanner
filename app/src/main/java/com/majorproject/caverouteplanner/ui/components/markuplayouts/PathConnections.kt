package com.majorproject.caverouteplanner.ui.components.markuplayouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

@Composable
fun PathConnectionsLayout(
    modifier: Modifier = Modifier,
){

    var currentlySelected by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End) {
        Text(
            text = "Markup Mode:"
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 1
            },
            text = "Annotate Path",
            currentlySelected = currentlySelected == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 2
            },
            text = "Remove Path Node",
            currentlySelected = currentlySelected == 2,
            removeButton = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 3
            },
            text = "Remove Path Edge",
            currentlySelected = currentlySelected == 3,
            removeButton = true
        )
    }
}