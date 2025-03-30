package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.PriorityQueue
import kotlin.math.pow

@Parcelize
data class RouteFinder(
    val sourceId: Int,
    val survey: Survey,
    val flags: Triple<Boolean, Boolean, Boolean>,
    val avoidEdges: List<Int> = listOf(),
    val numberOfTravellers: Int
) : Parcelable {
    @IgnoredOnParcel //may be because it can be recreated in init everytime
    var routeMap: MutableMap<SurveyNode?, SurveyPath?> = mutableMapOf()

    @IgnoredOnParcel
    var costMap: MutableMap<SurveyPath, Float> = mutableMapOf()

    init {
        val sourceNode =
            survey.pathNodes.find { it.id == sourceId } ?: throw Exception("Source node not found")

        val (noWater, noHardTraverse, highAltitude) = flags

        val visitedNodes = BooleanArray(survey.pathNodes.size)
        val routes: MutableMap<SurveyNode?, Pair<Float, SurveyPath?>> = mutableMapOf()
        val priorityQueue = PriorityQueue<Pair<Int, SurveyNode>>(compareBy { it.first })

        //map key is source id, value is pair of distance and edge id to get there
        routes.put(sourceNode, Pair(0f, null))
        priorityQueue.add(Pair(0, sourceNode))

        while (priorityQueue.isNotEmpty()) {
            val (currentDistance, currentNode) = priorityQueue.poll()

            if (visitedNodes[currentNode.id]) {
                continue
            }

            visitedNodes[currentNode.id] = true

            for (edgeId in currentNode.edges) {
                if (edgeId in avoidEdges) {
                    continue
                }
                val edge = survey.paths.find { it.id == edgeId }

                if (edge == null) {
                    continue
                }

                val neighbourId = edge.next(currentNode.id)
                val neighbour = survey.pathNodes.find { it.id == neighbourId }

                if (neighbour == null) {
                    continue
                }

                var weight = edge.distance
                if (noWater && edge.hasWater) {
                    weight *= 2f
                }
                if (noHardTraverse && edge.isHardTraverse) {
                    weight *= 2f
                }
                if (highAltitude) {
                    weight *= 0.5f.pow(edge.altitude)
                }

                val currentNodeRoute = routes[currentNode]
                val neighbourRoute = routes[neighbour]

                if (currentNodeRoute != null &&
                    currentNodeRoute.first + weight < (neighbourRoute?.first ?: Float.MAX_VALUE)
                ) {
                    val neighbourRoutePair = Pair(currentNodeRoute.first + weight, edge)

                    routes.put(neighbour, neighbourRoutePair)
                    costMap.put(edge, weight)

                    priorityQueue.add(Pair(neighbourRoutePair.first.toInt(), neighbour))
                }

            }
        }
        routeMap = routes.mapValues { it.value.second }.toMutableMap()
    }


    fun getRouteToNode(nodeId: Int): Route? {
        var currentNode = survey.pathNodes.find { it.id == nodeId } ?: return null
        var routeList: MutableList<MutableList<SurveyPath>> = mutableListOf()
        var totalDistance = 0f

        fun getEdgeFromNode(id: SurveyNode): Triple<SurveyPath, SurveyNode?, Float> {
            val foundEdge = routeMap[id]
            val neighbour =
                if (foundEdge?.ends?.first == id.id) foundEdge.ends.second else foundEdge?.ends?.first
            val neighbourNode = survey.pathNodes.find { it.id == neighbour }

            return Triple(foundEdge!!, neighbourNode, foundEdge.distance)
        }

        while (currentNode.id != sourceId) {
            val (edge, neighbour, newDistance) = getEdgeFromNode(currentNode)
            totalDistance += newDistance
            if (currentNode.isJunction) {
                routeList.add(0, mutableListOf(edge))
            } else {
                if (routeList.isEmpty()) {
                    routeList.add(mutableListOf())
                }
                routeList.first().add(0, edge)
            }
            currentNode = neighbour!!
        }
        if (routeList.isEmpty()) {
            return null
        }
        return Route(routeList = routeList, totalDistance = totalDistance, sourceNode = sourceId, numberOfTravellers = numberOfTravellers)
    }

    fun findNearestExit(): Int? {
        val exitNodes = survey.caveExits()
        var nearestExitDistance: Float = Float.MAX_VALUE
        var nearestExit: Int? = null

        for (exit in exitNodes) {
            if (getRouteToNode(exit) != null) {
                val exitRoute = getRouteToNode(exit)!!
                if (exitRoute.totalDistance < nearestExitDistance) {
                    nearestExitDistance = exitRoute.totalDistance
                    nearestExit = exit
                }
            }
        }
        return nearestExit
    }
}