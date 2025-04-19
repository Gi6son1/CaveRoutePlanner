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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillibits.composenumberpicker.HorizontalNumberPicker
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

/**
 * This composable is used to display the distance and compass calibration settings for the markup screen
 *
 * @param modifier The modifier to apply to the layout
 * @param updateCurrentlySelected The method used to update the currently selected setting
 * @param currentlySelectedSetting The currently selected setting
 * @param metersBetweenPoints The current number of metres between points
 * @param updatePixelsPerMeter The method used to update the number of pixels per metre
 */
@Composable
fun DistanceAndCompassCalibrationLayout(
    modifier: Modifier = Modifier,
    updateCurrentlySelected: (Int) -> Unit,
    currentlySelectedSetting: Int,
    metersBetweenPoints: Int,
    updatePixelsPerMeter: (Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = stringResource(R.string.calibrate_compass),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(1)
            },
            text = stringResource(R.string.north_compass),
            currentlySelected = currentlySelectedSetting == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(2)
            },
            text = stringResource(R.string.centre_compass),
            currentlySelected = currentlySelectedSetting == 2,
            buttonColour = Color(0xFF730606),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.calibrate_distance),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(3)
            },
            text = stringResource(R.string.distance_mark_1),
            currentlySelected = currentlySelectedSetting == 3,
            buttonColour = Color(0xFF228B22),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(4)
            },
            text = stringResource(R.string.distance_mark_2),
            currentlySelected = currentlySelectedSetting == 4,
            buttonColour = Color(0xFF228B22),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = stringResource(R.string.distance_in_metres),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ))
        HorizontalNumberPicker(
            min = 1,
            max = 50,
            onValueChange = {
                updatePixelsPerMeter(it)
            },
            default = metersBetweenPoints.coerceIn(1, 50)
        )
    }
}