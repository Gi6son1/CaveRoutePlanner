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
    @IgnoredOnParcel
    var endingNodes = mutableListOf<Int>()

    init {
        currentStage = 0
        startingNodes = mutableListOf<Int>(sourceNode)

        var previousPathEnds = routeList.first().last().ends

        routeList.forEachIndexed { index, pathList ->
            if (!pathList.isEmpty() && sourceNode != pathList.first().ends.first && sourceNode != pathList.first().ends.second){
                var currentPathEnds = pathList.first().ends

                if (previousPathEnds.first == currentPathEnds.first || previousPathEnds.first == currentPathEnds.second) {
                    startingNodes.add(previousPathEnds.first)
                    endingNodes.add(previousPathEnds.first)
                } else {
                    startingNodes.add(previousPathEnds.second)
                    endingNodes.add(previousPathEnds.second)
                }

                previousPathEnds = pathList.last().ends

                if (index == routeList.size - 1){
                    var secondToLastPathEnds = if (pathList.size - 2 < 0) {
                        routeList[index - 1 ].last().ends
                    } else {
                        pathList[pathList.size - 2].ends
                    }
                    if (previousPathEnds.first == secondToLastPathEnds.first || previousPathEnds.first == secondToLastPathEnds.second) {
                        endingNodes.add(previousPathEnds.second)
                    } else {
                        endingNodes.add(previousPathEnds.first)
                    }
                }
            }
        }
    }

    fun getCurrentStartingNode(): Int {
        return startingNodes[currentStage]
    }

    fun getCurrentEndingNode(): Int {
        return endingNodes[currentStage]
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