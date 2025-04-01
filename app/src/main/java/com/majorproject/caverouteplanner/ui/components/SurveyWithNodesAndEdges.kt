package com.majorproject.caverouteplanner.ui.components

import androidx.room.Embedded
import androidx.room.Relation

data class SurveyWithNodesAndEdges(
    @Embedded val survey: SurveyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val nodes: List<SurveyNodeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "surveyId"
    )
    val paths: List<SurveyPathEntity>
)
