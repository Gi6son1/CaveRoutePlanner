package com.majorproject.caverouteplanner.ui.components.screennavigation

/**
 * This file contains the sealed class for the different screens in the app, used to navigate between them with navigationGraph
 */
sealed class Screen(
    val route: String,
    val argument: String = ""
) {
    data object CaveListScreen : Screen("cave_list_screen")
    data object SurveyNavScreen : Screen("survey_nav_screen", "survey_id")
    data object SurveyMarkupScreen : Screen("survey_markup_screen")

    fun routePath() =
        if (argument.isNotEmpty())
            "$route/{$argument}"
        else
            route

    val basePath = "${route}/"

}