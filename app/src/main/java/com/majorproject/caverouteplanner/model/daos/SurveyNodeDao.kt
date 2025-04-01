package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.SurveyNodeEntity

@Dao
interface SurveyNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveyNode(surveyNode: SurveyNodeEntity)

    @Query("SELECT * FROM surveyNodes WHERE surveyId = :surveyId")
    fun getSurveyNodesBySurveyId(surveyId: Int): List<SurveyNodeEntity>

    @Query("SELECT * FROM surveyNodes WHERE id = :nodeId")
    fun getSurveyNodeById(nodeId: Int) : SurveyNodeEntity?
}