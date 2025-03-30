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

@Composable
fun CustomTripInfoBox(
    onClick: () -> Unit,
    isGoButton: Boolean = false,
    modifier: Modifier = Modifier,
    distance: Float,
    time: Int,
    pathNotDest: Boolean = false,
) {
    var distanceString: String
    if (distance < 200) {
        val newDistance = (distance * 1.094).toInt()
        distanceString = "${newDistance}yd"
    } else {
        val newDistance = distance / 1000
        distanceString = "${String.format(Locale.UK, "%.1f", newDistance)}km"
    }

    val timeString: String
    if (time < 60) {
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