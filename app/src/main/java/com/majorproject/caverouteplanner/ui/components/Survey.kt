package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.majorproject.caverouteplanner.ui.util.getNearestNode
import kotlinx.parcelize.Parcelize

/**
 * Data class to hold the old survey type, which now only applies to the ll survey
 * If more time was available, the survey would be moved to a JSON file and this class would be removed
 */
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

/**
 * This class holds the data for the survey properties table
 * @param id The id of the survey
 * @param imageReference The file reference to the image of the survey
 * @param pixelsPerMeter The number of pixels per meter in the survey
 * @param width The width of the survey
 * @param height The height of the survey
 * @param northAngle The angle of the survey with respect to the north
*/
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

/**
 * This class holds the data for a survey. It has a list of nodes and paths, and a survey properties object
 * @param properties The survey properties object
 * @param nodes The list of nodes
 * @param paths The list of paths
 */
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

    /**
     * This function returns the nearest node to the given point
     * @param pointCoordinates The point to find the nearest node to
     * @return The nearest node to the given point
     */
    fun nearestNode(pointCoordinates: Offset): SurveyNode? {
        return getNearestNode(pointCoordinates, nodes, properties.width, properties.height, 0.05f)
    }

    /**
     * This function returns the list of cave exits/entrances
     * @return The list of cave exits
     */
    fun caveExits(): List<SurveyNode> {
        return nodes.filter { it.isEntrance }
    }
}