package com.majorproject.caverouteplanner.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

@Composable
fun BackGroundScaffold(
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit,
){
    Scaffold(modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            pageContent(innerPadding)
        },
        containerColor = MaterialTheme.colorScheme.surfaceBright,
    )
}