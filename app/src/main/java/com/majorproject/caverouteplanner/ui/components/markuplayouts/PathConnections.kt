package com.majorproject.caverouteplanner.ui.components.markuplayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

@Composable
fun PathConnectionsLayout(
    modifier: Modifier = Modifier,
    updateCurrentlySelected: (Int) -> Unit,
    currentlySelectedSetting: Int
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "Markup Mode:",
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(1)
            },
            text = "Annotate Path",
            currentlySelected = currentlySelectedSetting == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(2)
            },
            text = "Remove Path",
            currentlySelected = currentlySelectedSetting == 2,
            removeButton = true
        )
    }
}