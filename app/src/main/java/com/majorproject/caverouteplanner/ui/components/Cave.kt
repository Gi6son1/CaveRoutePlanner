package com.majorproject.caverouteplanner.ui.components

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "caves",
    foreignKeys = [
        ForeignKey(
            entity = SurveyProperties::class,
            parentColumns = ["id"],
            childColumns = ["surveyId"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class CaveProperties(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val length: Int,
    val description: String,
    val difficulty: String,
    val location: String,
    val surveyId: Int
)

data class Cave(
    @Embedded val caveProperties: CaveProperties,
    @Relation(
        parentColumn = "surveyId",
        entityColumn = "id"
    )
    val surveyProperties: SurveyProperties
)