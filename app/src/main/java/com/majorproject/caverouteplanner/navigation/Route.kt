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
data class Route(val routeList: List<List<SurveyPath>>,
                 val totalDistance: Float,
                 val sourceNode: Int,
                 val numberOfTravellers: Int)
    : Parcelable {

    @IgnoredOnParcel
    var currentStage by mutableIntStateOf(-1)

    @IgnoredOnParcel
    var routeStarted: Boolean by mutableStateOf(false)

    @IgnoredOnParcel
    var pathDistances = mutableListOf<Float>()

    @IgnoredOnParcel
    var pathTravelTimes = mutableListOf<Int>()

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

                var currentNode = startNode
                var totalPathDistance = 0f
                var totalPathTime = 0f

                pathList.forEach { path ->
                    currentNode = path.next(currentNode)
                    totalPathDistance += path.distance

                    var timeForPath = (path.distance * 1.8) + (path.distance * (numberOfTravellers - 1) * 1.1f).toDouble()
                    if (path.hasWater) {
                        timeForPath *= 1.5
                    }
                    if (path.isHardTraverse) {
                        timeForPath *= 3
                        timeForPath += (numberOfTravellers - 1) * 5 // 5 mins per additional person
                    }
                    totalPathTime += timeForPath.toFloat()
                }

                pathDistances.add(totalPathDistance)



                pathTravelTimes.add(totalPathTime.toInt())


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

    fun getCurrentPathTravelTime(): Int {
        return pathTravelTimes[currentStage]
    }

    fun getTotalPathTravelTime(): Int {
        var totalTime = 0
        pathTravelTimes.forEach { time ->
            totalTime += time
        }
        return totalTime
    }

    fun getCurrentEndingNode(): Int {
        return endingNodes[currentStage]
    }

    fun getCurrentPathDistance(): Float {
        if (currentStage >= 0) {
            return pathDistances[currentStage]
        }
        return 0f
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