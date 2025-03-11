package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(val routeList: List<List<SurveyPath>>, val totalDistance: Float) : Parcelable {

    ////TODO make funciton that gets distance in current stage

    @IgnoredOnParcel
    var currentStage by mutableIntStateOf(0)

    init {
        currentStage = 0
    }

    fun getCurrentStage(): List<SurveyPath> {
        return routeList[currentStage]
    }

    fun previousStage(){
        if (currentStage > 0) currentStage--
    }

    fun nextStage(){
        if (currentStage < routeList.size - 1) currentStage++
    }

    fun resetStage() {
        currentStage = 0
    }
}