package com.majorproject.caverouteplanner.ui.components.markuplayouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.ui.components.customcomposables.CustomSmallTextButton

@Composable
fun AltitudesLayout(
    modifier: Modifier = Modifier,
    updateCurrentlySelected: (Int) -> Unit,
    currentlySelectedSetting: Int

){
    val altitudeColours = remember { listOf(
        Color(0xFF001433),
        Color(0xFF003366),
        Color(0xFF005C99),
        Color(0xFF0086A8),
        Color(0xFF00997A),
        Color(0xFF228B22),
        Color(0xFF6B8E23),
        Color(0xFFB8860B),
        Color(0xFFCD6600),
        Color(0xFFB22222),
        Color(0xFF800000)
    ) }

    Column(modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End) {
        Text(
            text = "Set altitude to:"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            for (i in -5..5) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomSmallTextButton(
                    onClick = {
                        updateCurrentlySelected(i)
                    },
                    text = i.toString(),
                    currentlySelected = currentlySelectedSetting == i,
                    buttonColour = altitudeColours[i + 5],
                    textColour = Color.White
                )
            }
        }
    }
}