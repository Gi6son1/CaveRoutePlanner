package com.majorproject.caverouteplanner.navigation

import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import java.util.PriorityQueue
import kotlin.math.pow

data class RouteFinder(val sourceId: Int, val survey: Survey, val flags: Triple<Boolean, Boolean, Boolean>) {
    var routeMap: MutableMap<Int, Int> = mutableMapOf()


    init {
        val (noWater, noHardTraverse, highAltitude) = flags

        val visitedNodes = BooleanArray(survey.pathNodes.size)
        val routes: MutableMap<Int, Pair<Float, Int>> = mutableMapOf()
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })

        routes.put(sourceId, Pair(0f, 0))
        priorityQueue.add(Pair(0, sourceId))

        while (priorityQueue.isNotEmpty()) {
            val (currentDistance, currentNodeId) = priorityQueue.poll()

            if (visitedNodes[currentNodeId]) {
                continue
            }

            visitedNodes[currentNodeId] = true

            for (edgeId in survey.pathNodes.find { it.id == currentNodeId }!!.edges) {
                val edge = survey.paths.find { it.id == edgeId }

                val neighbour = if (edge!!.ends.first == currentNodeId) edge.ends.second else edge.ends.first

                var weight = edge.distance
                if (noWater && edge.hasWater) {weight *= 1.5f}
                if (noHardTraverse && edge.isHardTraverse) {weight *= 1.5f}
                if (highAltitude){ weight *= 0.5f.pow(edge.altitude) }


                if (routes[currentNodeId]!!.first + weight < (routes[neighbour]?.first ?: Float.MAX_VALUE)) {
                    routes.put(neighbour, Pair(routes[currentNodeId]!!.first + weight, edgeId))

                    priorityQueue.add(Pair(routes[neighbour]!!.first.toInt(), neighbour))
                }
            }
        }
        routeMap = routes.mapValues { it.value.second }.toMutableMap()
    }


    fun getRouteToNode(id: Int): Pair<List<Int>, Float> {

        fun getEdgeFromNode(id: Int): Triple<SurveyPath, Int, Float> {
            val edge = routeMap[id] ?: -1
            val foundEdge = survey.paths.find { it.id == edge }
            val neighbour = if (foundEdge!!.ends.first == id) foundEdge.ends.second else foundEdge.ends.first

            return Triple(foundEdge, neighbour, foundEdge.distance)
        }
        var routeList = mutableListOf<Int>()
        var totalDistance = 0f

        var currentNode = id
        while (currentNode != sourceId) {
            val (edge, neighbour, newDistance) = getEdgeFromNode(currentNode)
            totalDistance += newDistance
            routeList.add(edge.id)
            currentNode = neighbour
        }
        return Pair(routeList.reversed(), totalDistance)
    }

}