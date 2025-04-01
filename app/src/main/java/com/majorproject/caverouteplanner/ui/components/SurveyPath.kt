package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.majorproject.caverouteplanner.datasource.util.PairConverter
import kotlinx.parcelize.Parcelize

//Parcelised so that it can be stored in remember saveables
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
    val distance: Float = 0.0f,
    val hasWater: Boolean = false,
    val altitude: Int = 0,
    val isHardTraverse: Boolean = false,
    val surveyId: Int
): Parcelable {
    fun next(currentId: Int) : Int {
        return if (ends.first - 1  == currentId) {
            ends.second - 1
        } else {
            ends.first - 1
        }
    }

    fun getPathEnds() = Pair(ends.first - 1, ends.second - 1)

    fun getPathId() = id - 1
}