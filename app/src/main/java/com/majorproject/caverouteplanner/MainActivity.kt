package com.majorproject.caverouteplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.majorproject.caverouteplanner.datasource.util.copyImageToInternalStorage
import com.majorproject.caverouteplanner.ui.components.llSurveyReference
import com.majorproject.caverouteplanner.ui.components.screennavigation.Screen
import com.majorproject.caverouteplanner.ui.navigationlayouts.SensorActivity
import com.majorproject.caverouteplanner.ui.screens.CaveListScreenTopLevel
import com.majorproject.caverouteplanner.ui.screens.SurveyMarkupScreen
import com.majorproject.caverouteplanner.ui.screens.SurveyNavScreenTopLevel
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sensorReading: Double? by mutableStateOf(null)

        SensorActivity(context = this,
            sensorReading = {
                sensorReading = it
            }
        )
        enableEdgeToEdge()
        setContent {
            CaveRoutePlannerTheme {
                SetupFiles()
                BuildNavigationGraph(sensorReading)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SensorActivity(
            context = this
        ).onResume()
    }

    override fun onPause() {
        super.onPause()
        SensorActivity(
            context = this
        ).onPause()
    }
}

@Composable
fun SetupFiles() {
    val context = LocalContext.current.applicationContext

    val internalStoragePath =
        copyImageToInternalStorage(context, "llygadlchwr.jpg", "llygadlchwr.jpg")

    if (internalStoragePath != null) {
        llSurveyReference.imageReference = internalStoragePath
    }
}

@Composable
private fun BuildNavigationGraph(sensorReading: Double?) {
    val navController = rememberNavController()
    var selectedSurveyId by remember { mutableIntStateOf(0) }


    NavHost(navController = navController, startDestination = Screen.CaveListScreen.route) {
        composable(Screen.CaveListScreen.routePath()) {
            CaveListScreenTopLevel(
                navigateToSurvey = { surveyId ->
                    val destination = "${Screen.SurveyNavScreen.basePath}${surveyId}"
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                },
                markupNewSurvey = {
                    navController.navigate(Screen.SurveyMarkupScreen.route){
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.SurveyNavScreen.routePath(),
            arguments = listOf(navArgument(Screen.SurveyNavScreen.argument) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                if (it.containsKey(Screen.SurveyNavScreen.argument)) {
                    selectedSurveyId = it.getInt(Screen.SurveyNavScreen.argument)
                }

                SurveyNavScreenTopLevel(
                    surveyId = selectedSurveyId,
                    backToMenu = {
                        navController.popBackStack(Screen.CaveListScreen.route, inclusive = false)
                    },
                    sensorReading = sensorReading
                )
            }
        }

        composable(route = Screen.SurveyMarkupScreen.routePath()) {
            SurveyMarkupScreen()
        }
    }

}