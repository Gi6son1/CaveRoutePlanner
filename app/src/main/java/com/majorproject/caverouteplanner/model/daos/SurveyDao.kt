package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.Survey

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurvey(cave: SurveyProperties): Long

    @Query("SELECT * FROM surveys WHERE id = :surveyId")
    fun getSurveyById(surveyId: Int): SurveyProperties?

    @Query("SELECT * FROM surveys")
    fun getAllSurveys(): List<SurveyProperties>

    @Transaction
    @Query("SELECT * FROM surveys WHERE id = :surveyId")
    fun getSurveyWithDataById(surveyId: Int): Survey?

    @Transaction
    @Query("SELECT * FROM surveys")
    fun getAllSurveysWithData(): List<Survey>

}