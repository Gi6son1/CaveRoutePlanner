package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    contentDescription: String? = null
){
    Button(
        onClick = { onClick() },
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
            .width(175.dp)
            .height(50.dp),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
    )
    {
        Row(modifier = Modifier.fillMaxSize().wrapContentSize()){
            Text(text = text,
                fontSize = 13.sp,
                modifier = Modifier.weight(0.75f)
                    .fillMaxSize()
                    .wrapContentSize(),
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxSize()
                    .wrapContentSize()
                    .size(22.dp)
            )
        }
    }
}