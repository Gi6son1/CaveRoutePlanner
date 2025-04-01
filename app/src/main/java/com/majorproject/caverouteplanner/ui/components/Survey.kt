package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.math.pow

@Parcelize
data class Survey(
    var id: Int = 0,
    var caveName: String = "",
    val imageReference: Int,
    var pathNodes: MutableList<SurveyNode>,
    var paths: MutableList<SurveyPath>,
    val pixelsPerMeter: Float,
    val size: Pair<Int, Int>
) : Parcelable {
    @Composable
    fun imageBitmap(): ImageBitmap {
        return ImageBitmap.imageResource(imageReference)
    }

    fun getNearestNode(pointCoordinates: Offset): Int? {
        val convertedX = pointCoordinates.x * size.first
        val convertedY = pointCoordinates.y * size.second
        val nearestNode = pathNodes.minByOrNull { (it.coordinates.first - convertedX).pow(2) + (it.coordinates.second - convertedY).pow(2) }
        return if (nearestNode != null) {
            val distance = (nearestNode.coordinates.first - convertedX).pow(2) + (nearestNode.coordinates.second - convertedY).pow(2)
            if (distance < (size.second.toFloat().pow(2) + (size.first.toFloat().pow(2)) /2) * 0.01)
                nearestNode.id
            else {
                null
            }
        } else {
            null
        }
    }

    fun caveExits(): List<Int> {
        return pathNodes.filter { it.isEntrance }.map { it.id }
    }
}

@Parcelize
@Entity(tableName = "surveys")
data class SurveyProperties(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageReference: String,
    val pixelsPerMeter: Float,
    val width: Int,
    val height: Int
) : Parcelable
