package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(val routeList: List<List<SurveyPath>>, val totalDistance: Float) : Parcelable {

    ////TODO make funciton that gets distance in current stage

    @IgnoredOnParcel
    var currentStage: Int

    init {
        currentStage = 0
    }

    fun getCurrentStage(): List<SurveyPath> {
        return routeList[currentStage]
    }

    fun previousStage(): List<SurveyPath> {
        if (currentStage > 0) currentStage--
        return getCurrentStage()
    }

    fun nextStage(): List<SurveyPath> {
        if (currentStage < routeList.size - 1) currentStage++
        return getCurrentStage()
    }

    fun resetStage() {
        currentStage = 0
    }
}