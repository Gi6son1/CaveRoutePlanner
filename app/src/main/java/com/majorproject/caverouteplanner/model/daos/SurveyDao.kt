package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.majorproject.caverouteplanner.ui.components.SurveyEntity
import com.majorproject.caverouteplanner.ui.components.SurveyWithNodesAndEdges

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurvey(cave: SurveyEntity): Long

    @Query("SELECT * FROM surveys WHERE id = :surveyId")
    fun getSurveyById(surveyId: Int): SurveyEntity?

    @Query("SELECT * FROM surveys")
    fun getAllSurveys(): List<SurveyEntity>

    @Transaction
    @Query("SELECT * FROM surveys WHERE id = :surveyId")
    fun getSurveyWithDataById(surveyId: Int): SurveyWithNodesAndEdges?

    @Transaction
    @Query("SELECT * FROM surveys")
    fun getAllSurveysWithData(): List<SurveyWithNodesAndEdges>

}