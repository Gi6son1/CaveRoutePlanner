package com.majorproject.caverouteplanner.ui.components.customcomposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.ui.components.Cave

@Composable
fun CollapsableListHeader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isExpanded: Boolean,
    locationString: String
){
    Row(
        modifier = modifier.clickable { onClick() }.fillMaxWidth().padding(horizontal = 5.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = locationString)

        Icon(
            imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
            contentDescription = "Expand button",
            modifier = Modifier.scale(scaleX = 2f, scaleY = 1.5f)
        )
    }
}

fun LazyListScope.Section(
    caveList: List<Cave>,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    navigateSurvey: (Int) -> Unit
) {
    item {
        CollapsableListHeader(
            onClick = onHeaderClick,
            isExpanded = isExpanded,
            locationString = caveList.first().caveProperties.location.trim().lowercase().replaceFirstChar { char -> char.uppercase() }
        )
    }

    if (isExpanded) {
        items(items = caveList){ cave ->
            CaveCardButton(cave = cave,
                onClick = {navigateSurvey(cave.caveProperties.surveyId)}
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

