package com.majorproject.caverouteplanner.ui.components

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * This class holds the data for the cave properties table
 *
 * @param id The id of the cave
 * @param name The name of the cave
 * @param length The length of the cave
 * @param description The description of the cave
 * @param difficulty The difficulty of the cave
 * @param location The location of the cave
 * @param surveyId The id of the survey that the cave belongs to
 */
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

/**
 * This class holds the cave, which is a combination of the cave properties and the survey properties
 * @param caveProperties The cave properties
 * @param surveyProperties The survey properties
 */
data class Cave(
    @Embedded val caveProperties: CaveProperties,
    @Relation(
        parentColumn = "surveyId",
        entityColumn = "id"
    )
    val surveyProperties: SurveyProperties
)