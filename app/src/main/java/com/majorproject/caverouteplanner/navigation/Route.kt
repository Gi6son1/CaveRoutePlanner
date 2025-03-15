package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(val routeList: List<List<SurveyPath>>, val totalDistance: Float, val sourceNode: Int) : Parcelable {

    ////TODO make funciton that gets distance in current stage

    @IgnoredOnParcel
    var currentStage by mutableIntStateOf(0)

    @IgnoredOnParcel
    var startingNodes = mutableListOf<Int>(sourceNode)

    init {
        currentStage = 0
        startingNodes = mutableListOf<Int>(sourceNode)

        var previousPathEnds = routeList.first().last().ends

        for (pathList in routeList) {
            if (pathList.isEmpty() || sourceNode == pathList.first().ends.first || sourceNode == pathList.first().ends.second){
                continue
            }

            var currentPathEnds = pathList.first().ends

            if (previousPathEnds.first == currentPathEnds.first || previousPathEnds.first == currentPathEnds.second) {
                startingNodes.add(previousPathEnds.first)
            } else {
                startingNodes.add(previousPathEnds.second)
            }

            previousPathEnds = pathList.last().ends

        }
    }

    fun getCurrentStartingNode(): Int {
        return startingNodes[currentStage]
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