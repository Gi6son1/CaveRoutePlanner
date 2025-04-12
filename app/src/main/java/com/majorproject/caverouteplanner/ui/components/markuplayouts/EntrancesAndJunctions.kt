package com.majorproject.caverouteplanner.ui.components.markuplayouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

@Composable
fun EntrancesAndJunctionsLayout(
    modifier: Modifier = Modifier,
){
    var currentlySelected by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End) {
        Text(
            text = "Mark as:"
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 1
            },
            text = "Entrance",
            currentlySelected = currentlySelected == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 2
            },
            text = "Junction",
            currentlySelected = currentlySelected == 2,
            buttonColour = Color(0xFF730606),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                currentlySelected = 3
            },
            text = "Remove",
            currentlySelected = currentlySelected == 3,
            removeButton = true
        )
    }
}