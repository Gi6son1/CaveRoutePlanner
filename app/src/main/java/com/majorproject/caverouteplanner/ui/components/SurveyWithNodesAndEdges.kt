package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.room.Embedded
import androidx.room.Relation
import com.majorproject.caverouteplanner.R
import kotlinx.parcelize.Parcelize
import kotlin.math.pow

@Parcelize
data class SurveyWithNodesAndEdges(
    @Embedded val survey: SurveyProperties,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val nodes: List<SurveyNodeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val paths: List<SurveyPathEntity>
): Parcelable{

    @Composable
    fun imageBitmap(): ImageBitmap {
        return ImageBitmap.imageResource(R.drawable.llygadlchwr)
    }

    fun getNearestNode(pointCoordinates: Offset): Int? {
        val convertedX = pointCoordinates.x * survey.width
        val convertedY = pointCoordinates.y * survey.height
        val nearestNode = nodes.minByOrNull { (it.x - convertedX).pow(2) + (it.y - convertedY).pow(2) }
        return if (nearestNode != null) {
            val distance = (nearestNode.x - convertedX).pow(2) + (nearestNode.y - convertedY).pow(2)
            if (distance < (survey.height.toFloat().pow(2) + (survey.width.toFloat().pow(2)) /2) * 0.01)
                nearestNode.getNodeId()
            else {
                null
            }
        } else {
            null
        }
    }

    fun caveExits(): List<Int> {
        return nodes.filter { it.isEntrance }.map { it.getNodeId() }
    }
}
