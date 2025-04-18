package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HelpMessageBox(
    modifier: Modifier = Modifier,
    message: String,
    boxHeight: Dp
) {
    Card(
        modifier = modifier
            .shadow(10.dp, shape = MaterialTheme.shapes.medium)
            .width(386.dp)
            .height(boxHeight)
            .border(1.dp, MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
                .padding(5.dp),
            textAlign = TextAlign.Center
        )
    }
}