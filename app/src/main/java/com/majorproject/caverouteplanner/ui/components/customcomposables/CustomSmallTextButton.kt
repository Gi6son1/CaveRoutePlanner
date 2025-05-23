package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * This composable is used to display a custom text button
 *
 * @param onClick The action to perform when the button is clicked
 * @param modifier The modifier to apply to the button
 * @param text The text to display on the button
 * @param currentlySelected Whether the button is currently selected
 * @param buttonColour The colour of the button
 * @param textColour The colour of the text
 * @param square Whether the button should be square
 * @param removeButton Whether the button should be a remove button
 */
@Composable
fun CustomSmallTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    currentlySelected: Boolean,
    buttonColour: Color = MaterialTheme.colorScheme.inverseSurface,
    textColour: Color = MaterialTheme.colorScheme.inverseOnSurface,
    square: Boolean = false,
    removeButton: Boolean = false
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .border(
                1.dp,
                if (removeButton) MaterialTheme.colorScheme.primaryContainer else Color.White,
                shape = MaterialTheme.shapes.medium
            )
            .width(if (square) 59.dp else 100.dp)
            .height(59.dp)
            .alpha(if (currentlySelected) 1f else 0.65f), //if the button is currently selected, make it more opaque
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(10.dp),
        elevation = ButtonDefaults.buttonElevation(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColour)
    )
    {
        Text(
            text = text,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textColour
        )
    }
}