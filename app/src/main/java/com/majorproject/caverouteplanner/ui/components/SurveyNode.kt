package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * This old class holds the node data for the ll survey, which now only applies to the ll survey
 */
@Parcelize
data class OldSurveyNodeType(
    var id: Int,
    var isEntrance: Boolean = false,
    var isJunction: Boolean = false,
    var coordinates: Pair<Int, Int>,
    var edges: MutableList<Int>
) : Parcelable

/**
 * This class holds data for an object in the survey node table
 * @param id The id of the node
 * @param isEntrance Whether the node is an entrance
 * @param isJunction Whether the node is a junction
 * @param x The x coordinate of the node
 * @param y The y coordinate of the node
 * @param surveyId The id of the survey that the node belongs to
 */
@Parcelize
@Entity(
    tableName = "surveynodes",
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
data class SurveyNode(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isEntrance: Boolean = false,
    val isJunction: Boolean = false,
    val x: Int,
    val y: Int,
    val surveyId: Int
) : Parcelable {
    /**
     * This function returns the id of the node - 1 is subtracted from the id to get the correct id since database ids start at 1, which breaks lists in the implementation
     */
    fun getNodeId() = id - 1
}
