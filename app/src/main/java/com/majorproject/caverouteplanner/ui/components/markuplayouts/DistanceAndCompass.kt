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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.chillibits.composenumberpicker.HorizontalNumberPicker
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

@Composable
fun DistanceAndCompassCalibrationLayout(
    modifier: Modifier = Modifier,
    updateCurrentlySelected: (Int) -> Unit,
    currentlySelectedSetting: Int,
    distBetweenPoints: Int,
    updateDistBetweenPoints: (Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "Calibrate compass:",
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(1)
            },
            text = "North Compass",
            currentlySelected = currentlySelectedSetting == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(2)
            },
            text = "Centre Compass",
            currentlySelected = currentlySelectedSetting == 2,
            buttonColour = Color(0xFF730606),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Calibrate distance:",
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(3)
            },
            text = "Distance Mark 1",
            currentlySelected = currentlySelectedSetting == 2,
            buttonColour = Color(0xFF228B22),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(4)
            },
            text = "Distance Mark 2",
            currentlySelected = currentlySelectedSetting == 2,
            buttonColour = Color(0xFF228B22),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Distance in metres:",
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ))
        HorizontalNumberPicker(
            min = 1,
            max = 50,
            onValueChange = {
                updateDistBetweenPoints(it)
            },
            default = distBetweenPoints
        )
    }
}