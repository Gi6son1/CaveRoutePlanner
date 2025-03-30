package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    invertedColour: Boolean = false,
    iconVector: ImageVector? = null,
    iconImage: Int? = null,
    contentDescription: String? = null
){
    FilledIconButton(
        onClick = { onClick() },
        modifier = modifier
            .shadow(10.dp, shape = CircleShape)
            .size(60.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (invertedColour) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        ),
    ) {
        if (iconVector != null){
            Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(60.dp),
                tint = if (invertedColour) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer
            )
        } else if (iconImage != null){
            Icon(
                painter = painterResource(id = iconImage),
                contentDescription = contentDescription,
                modifier = Modifier.size(60.dp),
                tint = if (invertedColour) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer
            )
        }

    }
}