package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.SurveyNode

@Dao
interface SurveyNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyNode(surveyNode: SurveyNode): Long

    @Query("SELECT * FROM surveyNodes WHERE surveyId = :surveyId")
    fun getSurveyNodesBySurveyId(surveyId: Int): List<SurveyNode>

    @Query("SELECT * FROM surveyNodes WHERE id = :nodeId")
    fun getSurveyNodeById(nodeId: Int) : SurveyNode?
}