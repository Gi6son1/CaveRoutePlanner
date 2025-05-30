package com.majorproject.caverouteplanner.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.model.viewmodel.CaveRoutePlannerViewModel
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.customcomposables.Section
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

/**
 * This file holds the composables for the cave list screen
 */

/**
 * Top level composable for the cave list screen
 * @param navigateToSurvey The function to navigate to the survey screen
 * @param markupNewSurvey The function to navigate to the survey markup screen
 * @param cavesViewModel The view model for the caves
 */
@Composable
fun CaveListScreenTopLevel(
    navigateToSurvey: (Int) -> Unit = {},
    markupNewSurvey: () -> Unit = {},
    cavesViewModel: CaveRoutePlannerViewModel
) {
    val caves = cavesViewModel.caveList.collectAsStateWithLifecycle() // collectAsStateWithLifecycle is used to convert the flow from the viewmodel to a readable state

    val caveLocationMap = mutableMapOf<String, MutableList<Cave>>()
    for (cave in caves.value){
        caveLocationMap.getOrPut(cave.caveProperties.location.uppercase().trim(), defaultValue = {mutableListOf()} ).add(cave)  //sorts caves into lists by location
    }
    val orderedList = caveLocationMap.values.toList().sortedBy { it.first().caveProperties.location.uppercase().trim() } //orders the lists by location alphabetically


    CaveListScreen(caveList = orderedList,
        navigateSurvey = { surveyId ->
            navigateToSurvey(surveyId)
        },
        markupNewSurvey = {
            markupNewSurvey()
        }
    )
}

/**
 * Composable for the cave list screen
 * @param caveList The list of caves to display
 * @param navigateSurvey The function to navigate to the survey screen
 * @param markupNewSurvey The function to navigate to the survey markup screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaveListScreen(
    caveList: List<List<Cave>>,
    navigateSurvey: (Int) -> Unit = {},
    markupNewSurvey: () -> Unit = {}
){
    BackGroundScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.cave_surveys)) },
            )
        },
        floatingActionButton = { //floating action button for adding a new survey
            LargeFloatingActionButton(onClick = { markupNewSurvey() },
                ){
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        },

    ) { innerPadding ->

        val isExpandedMap = remember {
            List(caveList.size) { index: Int -> index to true}.toMutableStateMap()
        }

        LazyColumn(modifier = Modifier //column to display the caves
            .padding(innerPadding)
            .fillMaxSize()
            .padding(10.dp)) {
            caveList.forEachIndexed {index, caveListSection ->
                Section(
                    caveList = caveListSection,
                    isExpanded = isExpandedMap[index] != false,
                    onHeaderClick = {
                        isExpandedMap[index] = isExpandedMap[index] == false
                    },
                    navigateSurvey = {
                        navigateSurvey(it)
                    }
                )

                if (index != caveList.size - 1){
                    item{
                        HorizontalDivider()
                    }
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
            listOf(listOf(
                Cave(
                    caveProperties = CaveProperties(
                        name = "Llygadlchwr",
                        length = 1200,
                        description = "Contains dry high level and an active river level, separated by sumps.",
                        difficulty = "Novice",
                        location = "South Wales",
                        surveyId = 1
                    ),
                    surveyProperties = SurveyProperties(
                        width = 1991,
                        height = 1429,
                        pixelsPerMeter = 14.600609f,
                        imageReference = "llygadlchwr.jpg",
                        northAngle = 0f
                    )
                )
            )
            )
        )
    }
}