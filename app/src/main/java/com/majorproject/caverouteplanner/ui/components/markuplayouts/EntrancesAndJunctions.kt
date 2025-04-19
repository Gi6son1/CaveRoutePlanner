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
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

/**
 * This composable is used to display the entrances and junctions settings for the markup screen - when the user is marking entrances and junctions
 * @param modifier The modifier to apply to the layout
 * @param updateCurrentlySelected The method used to update the currently selected setting
 * @param currentlySelectedSetting The currently selected setting
 */
@Composable
fun EntrancesAndJunctionsLayout(
    modifier: Modifier = Modifier,
    updateCurrentlySelected: (Int) -> Unit,
    currentlySelectedSetting: Int
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = stringResource(R.string.mark_as),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceBright,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(1)
            },
            text = stringResource(R.string.entrance),
            currentlySelected = currentlySelectedSetting == 1,
            buttonColour = Color(0xFF05166b),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(2)
            },
            text = stringResource(R.string.junction),
            currentlySelected = currentlySelectedSetting == 2,
            buttonColour = Color(0xFF730606),
            textColour = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomSmallTextButton(
            onClick = {
                updateCurrentlySelected(3)
            },
            text = stringResource(R.string.remove),
            currentlySelected = currentlySelectedSetting == 3,
            removeButton = true
        )
    }
}