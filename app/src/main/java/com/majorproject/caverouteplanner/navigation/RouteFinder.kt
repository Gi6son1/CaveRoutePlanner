package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyNodeEntity
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyPathEntity
import com.majorproject.caverouteplanner.ui.components.SurveyWithNodesAndEdges
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.PriorityQueue
import kotlin.math.pow

@Parcelize
data class RouteFinder(
    val sourceId: Int,
    val survey: SurveyWithNodesAndEdges,
    val flags: Triple<Boolean, Boolean, Boolean>,
    val avoidEdges: List<Int> = listOf(),
    val numberOfTravellers: Int
) : Parcelable {
    @IgnoredOnParcel //may be because it can be recreated in init everytime
    var routeMap: MutableMap<SurveyNodeEntity?, SurveyPathEntity?> = mutableMapOf()

    @IgnoredOnParcel
    var costMap: MutableMap<SurveyPathEntity, Float> = mutableMapOf()

    init {
        fun getNodeEdges(nodeId: Int): List<SurveyPathEntity> {
            var pathList: MutableList<SurveyPathEntity> = mutableListOf()

            survey.paths.forEach { path ->
                if (path.getPathEnds().first == nodeId || path.getPathEnds().second == nodeId) {
                    pathList.add(path)
                }
            }
            return pathList
        }

        val sourceNode =
            survey.nodes.find { it.getNodeId() == sourceId } ?: throw Exception("Source node not found")

        val (noWater, noHardTraverse, highAltitude) = flags

        val visitedNodes = BooleanArray(survey.nodes.size)
        val routes: MutableMap<SurveyNodeEntity?, Pair<Float, SurveyPathEntity?>> = mutableMapOf()
        val priorityQueue = PriorityQueue<Pair<Int, SurveyNodeEntity>>(compareBy { it.first })

        //map key is source id, value is pair of distance and edge id to get there
        routes.put(sourceNode, Pair(0f, null))
        priorityQueue.add(Pair(0, sourceNode))

        while (priorityQueue.isNotEmpty()) {
            val (currentDistance, currentNode) = priorityQueue.poll()

            if (visitedNodes[currentNode.getNodeId()]) {
                continue
            }

            visitedNodes[currentNode.getNodeId()] = true

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


    fun getRouteToNode(nodeId: Int): Route? {
        var currentNode = survey.nodes.find { it.getNodeId() == nodeId } ?: return null
        var routeList: MutableList<MutableList<SurveyPathEntity>> = mutableListOf()
        var totalDistance = 0f

        fun getEdgeFromNode(id: SurveyNodeEntity): Triple<SurveyPathEntity, SurveyNodeEntity?, Float> {
            val foundEdge = routeMap[id]
            val neighbour =
                if (foundEdge?.getPathEnds()?.first == id.getNodeId()) foundEdge.getPathEnds().second else foundEdge?.getPathEnds()?.first
            val neighbourNode = survey.nodes.find { it.getNodeId() == neighbour }

            return Triple(foundEdge!!, neighbourNode, foundEdge.distance)
        }

        while (currentNode.getNodeId() != sourceId) {
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
        return Route(
            routeList = routeList,
            totalDistance = totalDistance,
            sourceNode = sourceId,
            numberOfTravellers = numberOfTravellers
        )
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

