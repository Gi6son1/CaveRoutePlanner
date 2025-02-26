package com.majorproject.caverouteplanner.navigation

import com.majorproject.caverouteplanner.ui.components.Survey
import java.util.PriorityQueue

data class RouteFinder(val sourceId: Int, val survey: Survey) {
    private var shortestRoutes: MutableMap<Int, List<Int>> = mutableMapOf()
    val sourceDistances: FloatArray


    init {
        val visitedNodes = BooleanArray(survey.pathNodes.size)
        val distances = FloatArray(survey.pathNodes.size) { Float.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })

        distances[sourceId] = 0f
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
                val weight = edge.distance

                if (distances[currentNodeId] + weight < distances[neighbour]) {
                    distances[neighbour] = distances[currentNodeId] + weight
                    priorityQueue.add(Pair(distances[neighbour].toInt(), neighbour))
                }
            }
        }
        sourceDistances = distances
    }


    fun getRouteToNode(id: Int): List<Int> {
        return listOf()
    }

}