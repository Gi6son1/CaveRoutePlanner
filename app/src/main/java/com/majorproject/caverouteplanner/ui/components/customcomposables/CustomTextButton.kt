package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.round

/**
 * This composable is used to display a custom text button
 *
 * @param onClick The action to perform when the button is clicked
 * @param modifier The modifier to apply to the button
 * @param text The text to display on the button
 * @param iconVector The icon to display if it's a vector
 * @param iconImage The icon to display if it's an image
 * @param contentDescription The content description of the icon
 * @param flipped Whether the text and icon positions should be flipped
 */
@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    iconVector: ImageVector? = null,
    iconImage: Int? = null,
    contentDescription: String? = null,
    flipped: Boolean = false
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .width(187.dp)
            .height(59.dp),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(10.dp),
        elevation = ButtonDefaults.buttonElevation(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
    )
    {
        Row(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()) {
            if (!flipped) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxSize()
                        .wrapContentSize(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(1.dp))
            }
            if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxSize()
                        .wrapContentSize()
                        .size(30.dp)
                )
            } else if (iconImage != null) {
                Icon(
                    painter = painterResource(id = iconImage),
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxSize()
                        .wrapContentSize()
                        .size(30.dp)
                )
            }
            if (flipped) {
                Spacer(modifier = Modifier.width(1.dp))
                Text(
                    text = text,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxSize()
                        .wrapContentSize(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * This composable is used to display a custom trip info box - it's a modification of the custom button composable
 * @param onClick The action to perform when the button is clicked
 * @param isGoButton Whether the button should be a go button - makes it interactable if it is
 * @param modifier The modifier to apply to the button
 * @param distance The distance of the trip
 * @param time The time of the trip
 * @param pathNotDest Whether the text is supposed to be for a path, not the entire journey
 */
@Composable
fun CustomTripInfoBox(
    onClick: () -> Unit = {},
    isGoButton: Boolean = false,
    modifier: Modifier = Modifier,
    distance: Float,
    time: Int,
    pathNotDest: Boolean = false,
) {
    var roundedDistance = round(distance).toInt()
    var distanceString: String
    if (roundedDistance < 100) { //if the distance is less than 100m, display it in metres
        distanceString = "${roundedDistance}m"
    } else {
        val newDistance = distance / 1000
        distanceString = "${String.format(Locale.UK, "%.1f", newDistance)}km"
    }

    val timeString: String
    if (time < 60) { //if the time is less than 1 minute, display it in seconds
        timeString = "${time.toInt()}s"
    } else {
        val newTime = ceil(time / 60f).toInt()
        timeString = "${newTime}min"
    }

    Button(
        onClick = { if (isGoButton) onClick() },
        modifier = modifier
            .width(if (isGoButton) 386.dp else 286.dp)
            .shadow(if (isGoButton) 0.dp else 10.dp, shape = MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.scrim, shape = MaterialTheme.shapes.medium)
            .height(if (isGoButton) 68.dp else 58.dp),
        elevation = ButtonDefaults.buttonElevation(10.dp),
        shape = MaterialTheme.shapes.medium,
        enabled = isGoButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            disabledContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 1f),
            disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface
        )
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()) {
            if (isGoButton) {
                Text(
                    text = "GO",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.35f)
                        .wrapContentSize(),
                )
            }
            Text(
                text = distanceString, modifier = Modifier
                    .fillMaxSize()
                    .weight(0.5f)
                    .wrapContentSize(),
                fontSize = 25.sp
            )
            Icon(
                imageVector = if (pathNotDest) Icons.Outlined.ArrowUpward else Icons.Outlined.Flag,
                contentDescription = "Distance to point",
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxSize()
                    .wrapContentSize()
                    .size(40.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = timeString,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .wrapContentSize(),
                fontSize = 25.sp,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.DirectionsWalk,
                contentDescription = "Walk",
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxSize()
                    .wrapContentSize()
                    .size(40.dp),
            )
        }
    }
}