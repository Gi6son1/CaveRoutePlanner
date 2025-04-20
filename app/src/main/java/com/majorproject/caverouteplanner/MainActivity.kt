package com.majorproject.caverouteplanner

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.majorproject.caverouteplanner.datasource.SensorActivity
import com.majorproject.caverouteplanner.datasource.util.clearTempStorage
import com.majorproject.caverouteplanner.datasource.util.copyImageToInternalStorageFromAssets
import com.majorproject.caverouteplanner.datasource.util.saveUploadedImageToTempStorage
import com.majorproject.caverouteplanner.model.viewmodel.CaveRoutePlannerViewModel
import com.majorproject.caverouteplanner.ui.BackGroundScaffold
import com.majorproject.caverouteplanner.ui.components.llSurveyReference
import com.majorproject.caverouteplanner.ui.components.screennavigation.Screen
import com.majorproject.caverouteplanner.ui.screens.CaveListScreenTopLevel
import com.majorproject.caverouteplanner.ui.screens.SurveyMarkupScreenTopLevel
import com.majorproject.caverouteplanner.ui.screens.SurveyNavScreenTopLevel
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            var tempImagePath: String? by rememberSaveable { mutableStateOf(null) }

            val launcher = rememberLauncherForActivityResult( //create launcher for when image is required from the gallery for marking up a survey
                contract = PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        tempImagePath =
                            saveUploadedImageToTempStorage(uri, this.contentResolver, context) //once launched, save image to temp storage
                    }
                }
            )

            var setupComplete by rememberSaveable { mutableStateOf(false) }

            CaveRoutePlannerTheme {
                LaunchedEffect(true) {
                    launch {
                        val result = withContext(Dispatchers.IO) { //set up files on startup
                            setupFiles(context)
                        }
                        setupComplete = result
                    }
                }

                if (setupComplete) { //if setup is complete, show the app
                    BackGroundScaffold {
                        BuildNavigationGraph(
                            uploadImageCall = {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        mediaType = PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            uploadImagePath = tempImagePath,
                            clearTempStorage = {
                                clearTempStorage(context)
                                tempImagePath = null
                            }
                        )
                    }
                } else { //until setup is complete, show a blank menu screen
                    BackGroundScaffold(
                        topBar = { TopAppBar(title = {Text(stringResource(id = R.string.cave_surveys))} )}
                    ) {}
                }
            }
        }
    }
}

/**
 * Function that initialises the LL jpeg image into internal storage for the example survey
 *
 * @param context The context of the app
 * @return True if the setup was successful, false otherwise
 */
private fun setupFiles(context: Context): Boolean {
    val internalStoragePath =
        copyImageToInternalStorageFromAssets(context, "llygadlchwr.jpg", "llygadlchwr.jpg")

    if (internalStoragePath != null) {
        llSurveyReference.imageReference = internalStoragePath

    }
    return true
}

/**
 * Navigation graph for the app, this controls the navigation between screens
 *
 * @param uploadImageCall Function to call when an image is required from the gallery for marking up a survey
 * @param uploadImagePath Path to the image that is returned after uploading to temp storage
 * @param clearTempStorage Function to call when the temp storage is to be cleared
 * @param viewModel The view model for the app
 */
@Composable
private fun BuildNavigationGraph(
    uploadImageCall: () -> Unit,
    uploadImagePath: String? = null,
    clearTempStorage: () -> Unit,
    viewModel: CaveRoutePlannerViewModel = viewModel(),
) {
    val navController = rememberNavController()
    var selectedSurveyId by remember { mutableIntStateOf(0) }
    var navigateToMarkupScreen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uploadImagePath) { //when the image path is returned, navigate to the markup screen
        if (uploadImagePath != null) {
            navigateToMarkupScreen = true
            Log.d("LOGSDEBUG", "Image path: $uploadImagePath")
        }
    }

    if (navigateToMarkupScreen) { //if the image path is returned, navigate to the markup screen
        Log.d("LOGSDEBUG", "Navigating to markup screen")
        navController.navigate(Screen.SurveyMarkupScreen.route) { //navigate to the markup screen
            launchSingleTop = true
        }
        navigateToMarkupScreen = false //reset the flag
    }

    NavHost(navController = navController, startDestination = Screen.CaveListScreen.route) {
        composable(Screen.CaveListScreen.routePath(),
            enterTransition = { //animations for transitioning between screens
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(500)
                )
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(500)
                )
            }) {
            CaveListScreenTopLevel(
                navigateToSurvey = { surveyId ->
                    val destination = "${Screen.SurveyNavScreen.basePath}${surveyId}"
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                },
                markupNewSurvey = {
                    uploadImageCall()
                },
                cavesViewModel = viewModel
            )
        }

        composable(
            route = Screen.SurveyNavScreen.routePath(),
            arguments = listOf(navArgument(Screen.SurveyNavScreen.argument) {
                type = NavType.IntType
            }),
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(500)
                )
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(500)
                )
            }
        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                if (it.containsKey(Screen.SurveyNavScreen.argument)) {
                    selectedSurveyId = it.getInt(Screen.SurveyNavScreen.argument) //allows a survey Id to be passed to the survey nav screen
                }

                SurveyNavScreenTopLevel(
                    surveyId = selectedSurveyId,
                    backToMenu = {
                        navController.popBackStack(Screen.CaveListScreen.route, inclusive = false)
                    },
                    surveyViewModel = viewModel
                )
            }
        }

        composable(route = Screen.SurveyMarkupScreen.routePath(),
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(500)
                )
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(500)
                )
            }) {
            SurveyMarkupScreenTopLevel(
                returnToMenu = {
                    navController.popBackStack(Screen.CaveListScreen.route, inclusive = false)
                    clearTempStorage()
                },
                viewModel = viewModel
            )
        }
    }

}