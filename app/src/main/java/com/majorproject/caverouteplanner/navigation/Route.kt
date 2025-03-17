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
    var startingNodes = mutableListOf<Int>()
    @IgnoredOnParcel
    var endingNodes = mutableListOf<Int>()

    init {
        currentStage = 0

        var previousPathEnds = routeList.first().last().ends

        routeList.forEachIndexed { index, pathList ->
            if (pathList.isNotEmpty()){
                var startNode: Int

                var currentPathEnds = pathList.first().ends

                if (index == 0) {
                    startNode = sourceNode
                    startingNodes.add(startNode)
                } else {
                    startNode = if (previousPathEnds.first == currentPathEnds.first || previousPathEnds.first == currentPathEnds.second) {
                        previousPathEnds.first
                    } else {
                        previousPathEnds.second
                    }

                    startingNodes.add(startNode)
                    endingNodes.add(startNode)
                }

                if (index == routeList.size - 1) {
                    var currentNode = startNode
                    for (path in pathList) {
                        currentNode = path.next(currentNode)
                    }
                    endingNodes.add(currentNode)
                }

                previousPathEnds = pathList.last().ends
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