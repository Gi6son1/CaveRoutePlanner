package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyNode(
    var id: Int,
    var isEntrance: Boolean = false,
    var isJunction: Boolean = false,
    var coordinates: Pair<Int, Int>,
    var edges: MutableList<Int>
) : Parcelable

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
data class SurveyNodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isEntrance: Boolean = false,
    val isJunction: Boolean = false,
    val x: Int,
    val y: Int,
    val surveyId: Int
) : Parcelable {
    fun getNodeId() = id - 1
}
