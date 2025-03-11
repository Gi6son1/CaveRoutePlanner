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
    val flags: Triple<Boolean, Boolean, Boolean> = Triple(false, false, false),
    val avoidEdges: List<Int> = listOf()
) : Parcelable {
    @IgnoredOnParcel //may be because it can be recreated in init everytime
    var routeMap: MutableMap<SurveyNode?, SurveyPath?> = mutableMapOf()
    @IgnoredOnParcel
    var costMap: MutableMap<SurveyPath, Float> = mutableMapOf()

    @IgnoredOnParcel
    var currentRoute: Route? = null


    init {
        val sourceNode = survey.pathNodes.find { it.id == sourceId} ?: throw Exception("Source node not found")

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

                val neighbourId = if (edge!!.ends.first == currentNode.id) edge.ends.second else edge.ends.first
                val neighbour = survey.pathNodes.find { it.id == neighbourId }

                var weight = edge.distance
                if (noWater && edge.hasWater) {weight *= 1.5f}
                if (noHardTraverse && edge.isHardTraverse) {weight *= 1.5f}
                if (highAltitude){ weight *= 0.5f.pow(edge.altitude) }


                if (routes[currentNode]!!.first + weight < (routes[neighbour]?.first ?: Float.MAX_VALUE)) {
                    routes.put(neighbour, Pair(routes[currentNode]!!.first + weight, edge))
                    costMap.put(edge, weight)

                    priorityQueue.add(Pair(routes[neighbour]!!.first.toInt(), neighbour!!))
                }
            }
        }
        routeMap = routes.mapValues { it.value.second }.toMutableMap()
    }


    fun setRouteToNode(nodeId: Int) {
        var currentNode = survey.pathNodes.find { it.id == nodeId } ?: return
        var routeList: MutableList<MutableList<SurveyPath>> = mutableListOf()
        var totalDistance = 0f

        fun getEdgeFromNode(id: SurveyNode): Triple<SurveyPath, SurveyNode?, Float> {
            val foundEdge = routeMap[id]
            val neighbour = if (foundEdge?.ends?.first == id.id) foundEdge.ends.second else foundEdge?.ends?.first
            val neighbourNode = survey.pathNodes.find { it.id == neighbour }

            return Triple(foundEdge!!, neighbourNode, foundEdge.distance)
        }


        while (currentNode.id != sourceId) {
            val (edge, neighbour, newDistance) = getEdgeFromNode(currentNode)
            totalDistance += newDistance
            if (routeList.isEmpty() || currentNode.isJunction) {
                routeList.add(mutableListOf(edge))
            } else {
                routeList.last().add(edge)
            }
            currentNode = neighbour!!
        }
        currentRoute =  Route(routeList = routeList.reversed(), totalDistance = totalDistance)
    }
}