package com.majorproject.caverouteplanner.navigation

import android.os.Parcelable
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.Survey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.PriorityQueue
import kotlin.math.pow

/**
 * This class holds the data for the route finder, used for finding the shortest path from the source node to every other node in the survey
 *
 * @param sourceNode The source node of the route
 * @param survey The survey to find the route in
 * @param flags The flags for the route finder - whether to avoid water, hard traverses, or high altitude preferred
 * @param avoidEdges A list of edges to avoid - not currently in use, but may be in the future, when some paths cannot be included in the navigation
 * @param numberOfTravellers The number of travellers in the route - used for passing to the route object
 */
@Parcelize
data class RouteFinder(
    val sourceNode: SurveyNode,
    val survey: Survey,
    val flags: Triple<Boolean, Boolean, Boolean>,
    val avoidEdges: List<Int> = listOf(),
    val numberOfTravellers: Int
) : Parcelable {
    @IgnoredOnParcel //may be because it can be recreated in init everytime
    private var routeMap: MutableMap<SurveyNode?, SurveyPath?> = mutableMapOf() //map of nodes, showing the edge that is the best one to take to get to the source node

    @IgnoredOnParcel
    private var costMap: MutableMap<SurveyPath, Float> = mutableMapOf() //map of edges, showing the cost of getting to the source node using that edge


    /**
     * This initialises the routeFinder, calculating the shortest path from the source node to every other node in the survey
     * It's a modification of Dijkstra's algorithm, the difference being that the shortest path cost isn't stored, but the edge that gave the shortest path is stored instead
     */
    init {
        /**
         * This function returns a list of edges that start or end at the given node
         */
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
            val currentNode = priorityQueue.poll().second

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
                    currentNodeRoute.first + weight < (neighbourRoute?.first ?: Float.MAX_VALUE) //if the new route is shorter than the current route, or if there is no current route, then update it
                ) {
                    val neighbourRoutePair = Pair(currentNodeRoute.first + weight, edge)

                    routes.put(neighbour, neighbourRoutePair)
                    costMap.put(edge, weight)

                    priorityQueue.add(Pair(neighbourRoutePair.first.toInt(), neighbour))
                }

            }
        }
        routeMap = routes.mapValues { it.value.second }.toMutableMap() //sets the routeMap to the edge that is the best one to take to get to the source node
    }


    /**
     * This function returns a route to the given node, if one exists
     *
     * @param node The node to find a route to
     *
     * @return A route to the given node, or null if one does not exist
     */
    fun getRouteToNode(node: SurveyNode?): Route? {
        var currentNode = node ?: return null
        var routeList: MutableList<MutableList<SurveyPath>> = mutableListOf()
        var totalDistance = 0f

        /**
         * This function is used to retrieve the edge from the given node that is the best one to take to get to the source
         *
         * @param id The node to get the edge from
         * @return A triple containing the edge, the other node that the edge connects to, and the distance of the edge
         */
        fun getEdgeFromNode(id: SurveyNode): Triple<SurveyPath?, SurveyNode?, Float> {
            val foundEdge = routeMap[id]
            if (foundEdge == null) { //if there is no edge, then there is no route to the source node
                return Triple(null, null, 0f)
            }
            val neighbour =
                if (foundEdge.getPathEnds().first == id.getNodeId()) foundEdge.getPathEnds().second else foundEdge.getPathEnds().first
            val neighbourNode = survey.nodes.find { it.getNodeId() == neighbour }

            return Triple(foundEdge, neighbourNode, foundEdge.distance)
        }

        while (currentNode.getNodeId() != sourceNode.getNodeId()) { //while the current node is not the source node
            val (edge, neighbour, newDistance) = getEdgeFromNode(currentNode) //get the edge from the current node
            if (edge == null) {
                return null
            }
            totalDistance += newDistance //add the distance of the edge to the total distance
            if (currentNode.isJunction) { //if the current node is a junction, then add the edge to the routeList under a new list to hold the new path
                routeList.add(0, mutableListOf(edge))
            } else {
                if (routeList.isEmpty()) { //if the routeList is empty, then add a new list to hold the new path
                    routeList.add(mutableListOf())
                }
                routeList.first().add(0, edge)
            }
            currentNode = neighbour!!
        }
        if (routeList.isEmpty()) {
            return null
        }
        return Route( //return the route object
            routeList = routeList,
            totalDistance = totalDistance,
            sourceNode = sourceNode.getNodeId(),
            numberOfTravellers = numberOfTravellers
        )
    }

    /**
     * This function finds the nearest exit to the survey from the source node
     *
     * @return The nearest exit to the survey, or null if one does not exist
     */
    fun findNearestExit(): SurveyNode? {
        val exitNodes = survey.caveExits()
        var nearestExitDistance: Float = Float.MAX_VALUE
        var nearestExit: SurveyNode? = null

        for (exit in exitNodes) { //for each exit node, find the route to it
            if (getRouteToNode(exit) != null) {
                val exitRoute = getRouteToNode(exit)!!
                if (exitRoute.totalDistance < nearestExitDistance) { //if the route is shorter than the current nearest exit, then update the nearest exit
                    nearestExitDistance = exitRoute.totalDistance
                    nearestExit = exit
                }
            }
        }
        return nearestExit
    }
}

