package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.Survey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.PriorityQueue
import kotlin.math.pow

@Parcelize
data class RouteFinder(
    val sourceNode: SurveyNode,
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
        fun getNodeEdges(nodeId: Int): List<SurveyPath> {
            var pathList: MutableList<SurveyPath> = mutableListOf()

            survey.paths.forEach { path ->
                if (path.getPathEnds().first == nodeId || path.getPathEnds().second == nodeId) {
                    pathList.add(path)
                }
            }
            return pathList
        }

        val (noWater, noHardTraverse, highAltitude) = flags

        val visitedNodes = BooleanArray(survey.nodes.size)
        val routes: MutableMap<SurveyNode?, Pair<Float, SurveyPath?>> = mutableMapOf()
        val priorityQueue = PriorityQueue<Pair<Int, SurveyNode>>(compareBy { it.first })

        //map key is source id, value is pair of distance and edge id to get there
        routes.put(sourceNode, Pair(0f, null))
        priorityQueue.add(Pair(0, sourceNode))

        while (priorityQueue.isNotEmpty()) {
            val (currentDistance, currentNode) = priorityQueue.poll()

            if (visitedNodes[survey.nodes.indexOf(currentNode)]) {
                continue
            }

            visitedNodes[survey.nodes.indexOf(currentNode)] = true

            for (edge in getNodeEdges(currentNode.getNodeId())) {
                if (edge.getPathId() in avoidEdges) {
                    continue
                }

                val neighbourId = edge.next(currentNode.getNodeId())
                val neighbour = survey.nodes.find { it.getNodeId() == neighbourId }

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


    fun getRouteToNode(node: SurveyNode?): Route? {
        var currentNode = node ?: return null
        var routeList: MutableList<MutableList<SurveyPath>> = mutableListOf()
        var totalDistance = 0f

        fun getEdgeFromNode(id: SurveyNode): Triple<SurveyPath?, SurveyNode?, Float> {
            val foundEdge = routeMap[id]
            if (foundEdge == null) {
                return Triple(null, null, 0f)
            }
            val neighbour =
                if (foundEdge.getPathEnds().first == id.getNodeId()) foundEdge.getPathEnds().second else foundEdge.getPathEnds()?.first
            val neighbourNode = survey.nodes.find { it.getNodeId() == neighbour }

            return Triple(foundEdge, neighbourNode, foundEdge.distance)
        }

        while (currentNode.getNodeId() != sourceNode.getNodeId()) {
            val (edge, neighbour, newDistance) = getEdgeFromNode(currentNode)
            if (edge == null) {
                return null
            }
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
        return Route(
            routeList = routeList,
            totalDistance = totalDistance,
            sourceNode = sourceNode.getNodeId(),
            numberOfTravellers = numberOfTravellers
        )
    }

    fun findNearestExit(): SurveyNode? {
        val exitNodes = survey.caveExits()
        var nearestExitDistance: Float = Float.MAX_VALUE
        var nearestExit: SurveyNode? = null

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

