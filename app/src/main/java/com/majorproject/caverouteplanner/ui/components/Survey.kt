package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.majorproject.caverouteplanner.datasource.util.getBitmapFromInternalStorage
import com.majorproject.caverouteplanner.ui.util.getNearestNode
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
    var northAngle: Float,
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
    val height: Int,
    val northAngle: Float
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
        return getBitmapFromInternalStorage(properties.imageReference)
    }

    fun nearestNode(pointCoordinates: Offset): SurveyNode? {
        return getNearestNode(pointCoordinates, nodes, properties.width, properties.height, 0.05f)
    }

    fun caveExits(): List<SurveyNode> {
        return nodes.filter { it.isEntrance }
    }
}