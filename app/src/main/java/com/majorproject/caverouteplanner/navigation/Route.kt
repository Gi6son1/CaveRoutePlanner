package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(val routeList: List<List<SurveyPath>>, val totalDistance: Float, val sourceNode: Int) : Parcelable {

    ////TODO make funciton that gets distance in current stage

    @IgnoredOnParcel
    var currentStage by mutableIntStateOf(-1)

    @IgnoredOnParcel
    var routeStarted: Boolean by mutableStateOf(false)

    @IgnoredOnParcel
    var startingNodes = mutableListOf<Int>()
    @IgnoredOnParcel
    var endingNodes = mutableListOf<Int>()

    init {
        currentStage = -1
        routeStarted = false

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

    fun beginJourney(){
        routeStarted = true
        nextStage()
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
        if (currentStage > 0 && routeStarted) currentStage--
    }

    fun nextStage(){
        if (currentStage < routeList.size - 1 && routeStarted) currentStage++
    }

    fun resetStage() {
        currentStage = 0
    }
}