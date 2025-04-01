package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.SurveyPathEntity

@Dao
interface SurveyPathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyPath(surveyPath: SurveyPathEntity): Long

    @Query("SELECT * FROM surveyPaths WHERE surveyId = :surveyId")
    fun getSurveyPathsBySurveyId(surveyId: Int): List<SurveyPathEntity>

    @Query("SELECT * FROM surveyPaths WHERE id = :pathId")
    fun getSurveyPathById(pathId: Int) : SurveyPathEntity?

}