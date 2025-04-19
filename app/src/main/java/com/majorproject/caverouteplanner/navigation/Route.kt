package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * This class holds the route data for when a journey is in progress
 *
 * @param routeList A list of lists of survey paths, representing the route
 * @param totalDistance The total distance of the route
 * @param sourceNode The source node of the route
 * @param numberOfTravellers The number of travellers in the route - used for calculating the time it takes to complete the route
 */
@Parcelize //this is used so that the survey can used properly with rememberSaveable
data class Route(val routeList: List<List<SurveyPath>>,
                 val totalDistance: Float,
                 val sourceNode: Int,
                 val numberOfTravellers: Int)
    : Parcelable {

    @IgnoredOnParcel
    var currentStage by mutableIntStateOf(-1) //this is used to keep track of the current stage of the route

    @IgnoredOnParcel
    var routeStarted: Boolean by mutableStateOf(false) //this is used to keep track of whether the route has started

    @IgnoredOnParcel
    var pathDistances = mutableListOf<Float>() //this is used to keep track of the distance of each path

    @IgnoredOnParcel
    var pathTravelTimes = mutableListOf<Int>() //this is used to keep track of the time it takes to complete each path

    @IgnoredOnParcel
    var startingNodes = mutableListOf<Int>() //this is used to keep track of the starting node of each path
    @IgnoredOnParcel
    var endingNodes = mutableListOf<Int>() //this is used to keep track of the ending node of each path

    //upon initialisation, all of the above variables are calculated
    init {
        currentStage = -1
        routeStarted = false

        var previousPathEnds = routeList.first().last().getPathEnds()

        routeList.forEachIndexed { index, pathList ->
            if (pathList.isNotEmpty()){
                var startNode: Int

                var currentPathEnds = pathList.first().getPathEnds()

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
                        timeForPath *= 1.5 //increases time if the path has water
                    }
                    if (path.isHardTraverse) {
                        timeForPath *= 3 //increases time if the path has a hard traverse
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

                previousPathEnds = pathList.last().getPathEnds()
            }
        }
    }

    /**
     * This function starts the journey and moves to the first stage
     */
    fun beginJourney(){
        routeStarted = true
        nextStage()
    }

    /**
     * This function returns the current starting node of the route
     *
     * @return The current starting node
     */
    fun getCurrentStartingNode(): Int {
        return startingNodes[currentStage]
    }

    /**
     * This function returns the current path travel time
     *
     * @return The current path travel time
     */
    fun getCurrentPathTravelTime(): Int {
        return pathTravelTimes[currentStage]
    }

    /**
     * This function returns the total path travel time
     *
     * @return The total path travel time
     */
    fun getTotalPathTravelTime(): Int {
        var totalTime = 0
        pathTravelTimes.forEach { time ->
            totalTime += time
        }
        return totalTime
    }

    /**
     * This function returns the current ending node of the route
     *
     * @return The current ending node
     */
    fun getCurrentEndingNode(): Int {
        return endingNodes[currentStage]
    }

    /**
     * This function returns the current path distance
     *
     * @return The current path distance, or 0 if the route has not started
     */
    fun getCurrentPathDistance(): Float {
        if (currentStage >= 0) {
            return pathDistances[currentStage]
        }
        return 0f
    }

    /**
     * This function returns the current stage of the route
     *
     * @return The list of survey paths in the current stage
     */
    fun getCurrentStage(): List<SurveyPath> {
        return routeList[currentStage]
    }

    /**
     * This function moves to the previous stage of the route
     */
    fun previousStage(){
        if (currentStage > 0 && routeStarted) currentStage--
    }

    /**
     * This function moves to the next stage of the route
     */
    fun nextStage(){
        if (currentStage < routeList.size - 1 && routeStarted) currentStage++
    }
}