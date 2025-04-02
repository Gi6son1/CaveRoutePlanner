package com.majorproject.caverouteplanner.ui.screens

import androidx.compose.runtime.Composable
import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.customcomposables.CaveCardButton
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

@Composable
fun CaveListScreenTopLevel(

) {
    val context = LocalContext.current.applicationContext
    val repository = CaveRoutePlannerRepository(context as Application)
    val caves = repository.getAllCaves()

    CaveListScreen(caves)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaveListScreen(
    caveList: List<Cave>,
    navigateSurvey: (Int) -> Unit = {}
){
    BackGroundScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Cave Surveys") },
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(onClick = { /*TODO*/ },
                ){
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        },

    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(10.dp)) {
            caveList.forEach { cave ->
                item {
                    CaveCardButton(
                        cave = cave,
                        onClick = { navigateSurvey(cave.caveProperties.surveyId) }
                    )
                }
            }
        }
    }

}


@Preview
@Composable
fun CaveListScreenPreview(){
    CaveRoutePlannerTheme {
        CaveListScreen(
            listOf(
                Cave(
                    caveProperties = CaveProperties(
                        name = "Llygadlchwr",
                        length = 1.2f,
                        description = "Contains dry high level and an active river level, separated by sumps.",
                        difficulty = "Novice",
                        location = "South Wales",
                        surveyId = 1
                    ),
                    surveyProperties = SurveyProperties(
                        width = 1991,
                        height = 1429,
                        pixelsPerMeter = 14.600609f,
                        imageReference = "llygadlchwr.jpg"
                    )
                )
            )
        )
    }
}