package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.majorproject.caverouteplanner.datasource.util.PairConverter
import kotlinx.parcelize.Parcelize

/**
 * This old class holds the path data for the ll survey, which now only applies to the ll survey
 */
@Parcelize
data class OldSurveyPathType(
    var id: Int,
    var ends: Pair<Int, Int>,
    var distance: Float = 0.0f,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false
) : Parcelable {
    fun next(currentId: Int) : Int {
        return if (ends.first == currentId) {
            ends.second
        } else {
            ends.first
        }
    }
}

/**
 * This class holds data for an object in the survey node table
 * @param id The id of the path
 * @param ends The ends of the path
 * @param distance The distance of the path
 * @param hasWater Whether the path has water
 * @param altitude The altitude of the path
 * @param isHardTraverse Whether the path is hard to traverse
 * @param surveyId The id of the survey that the path belongs to
 */
@Parcelize
@Entity(
    tableName = "surveypaths",
    foreignKeys = [
        ForeignKey(
            entity = SurveyProperties::class,
            parentColumns = ["id"],
            childColumns = ["surveyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["surveyId"])]
)
data class SurveyPath(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @TypeConverters(PairConverter::class)
    val ends: Pair<Int, Int>,
    var distance: Float = 0.0f,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false,
    val surveyId: Int
): Parcelable {

    /**
     * this function returns the id of the other node connected to the path
     *
     * @param currentId the id of the current node
     * @return the id of the other node connected to the path
     */
    fun next(currentId: Int) : Int {
        return if (ends.first - 1  == currentId) {
            ends.second - 1
        } else {
            ends.first - 1
        }
    }

    /**
     * this function returns the ends of the path as a pair of ints, modified to account for database id differences
     */
    fun getPathEnds() = Pair(ends.first - 1, ends.second - 1)

    /**
     * this function returns the id of the path - 1 is subtracted from the id to get the correct id since database ids start at 1, which breaks lists in the implementation
     */
    fun getPathId() = id - 1
}