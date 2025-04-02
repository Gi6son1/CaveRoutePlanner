package com.majorproject.caverouteplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.majorproject.caverouteplanner.ui.components.screennavigation.Screen

import com.majorproject.caverouteplanner.ui.screens.CaveListScreenTopLevel
import com.majorproject.caverouteplanner.ui.screens.SurveyNavScreen
import com.majorproject.caverouteplanner.ui.screens.SurveyNavScreenTopLevel
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaveRoutePlannerTheme {
                BuildNavigationGraph()
            }
        }
    }
}

@Composable
private fun BuildNavigationGraph(){
    val navController = rememberNavController()
    var selectedSurveyId by remember { mutableIntStateOf(0) }

    NavHost(navController = navController, startDestination = Screen.CaveListScreen.route){
        composable(Screen.CaveListScreen.routePath()){
            CaveListScreenTopLevel(navigateToSurvey = { surveyId ->
                val destination = "${Screen.SurveyNavScreen.basePath}${surveyId}"
                navController.navigate(destination) {
                    launchSingleTop = true
                }
            })
        }

        composable(route = Screen.SurveyNavScreen.routePath(),
            arguments = listOf(navArgument(Screen.SurveyNavScreen.argument) {
                type = NavType.IntType
            })
        ){ backStackEntry ->
            backStackEntry.arguments?.let {
                if (it.containsKey(Screen.SurveyNavScreen.argument)){
                    selectedSurveyId = it.getInt(Screen.SurveyNavScreen.argument)
                }

                SurveyNavScreenTopLevel(surveyId = selectedSurveyId,
                    backToMenu = {
                        navController.popBackStack(Screen.CaveListScreen.route, inclusive = false)
                    }
                )
            }
        }
    }

}