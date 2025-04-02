package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.majorproject.caverouteplanner.R
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromInternalStorage
import kotlinx.parcelize.Parcelize
import kotlin.math.pow

@Parcelize
data class OldSurveyType(
    var id: Int = 0,
    var caveName: String = "",
    var imageReference: String = "",
    var pathNodes: MutableList<OldSurveyNodeType>,
    var paths: MutableList<OldSurveyPathType>,
    val pixelsPerMeter: Float,
    val size: Pair<Int, Int>
) : Parcelable

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

@Parcelize
data class Survey(
    @Embedded val properties: SurveyProperties,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val nodes: List<SurveyNode>,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val paths: List<SurveyPath>
): Parcelable{

    @Composable
    fun imageBitmap(): ImageBitmap? {
        return getBitmapFromInternalStorage(LocalContext.current, properties.imageReference)
    }

    fun getNearestNode(pointCoordinates: Offset): Int? {
        val convertedX = pointCoordinates.x * properties.width
        val convertedY = pointCoordinates.y * properties.height
        val nearestNode = nodes.minByOrNull { (it.x - convertedX).pow(2) + (it.y - convertedY).pow(2) }
        return if (nearestNode != null) {
            val distance = (nearestNode.x - convertedX).pow(2) + (nearestNode.y - convertedY).pow(2)
            if (distance < (properties.height.toFloat().pow(2) + (properties.width.toFloat().pow(2)) /2) * 0.01)
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