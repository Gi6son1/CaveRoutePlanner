package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import kotlinx.coroutines.flow.Flow

/**
 * This interface holds the database queries for the survey paths
 */
@Dao
interface SurveyPathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveyPath(surveyPath: SurveyPath): Long

    @Query("SELECT * FROM surveyPaths WHERE surveyId = :surveyId")
    fun getSurveyPathsBySurveyId(surveyId: Int): Flow<List<SurveyPath>>

    @Query("SELECT * FROM surveyPaths WHERE id = :pathId")
    fun getSurveyPathById(pathId: Int) : Flow<SurveyPath?>

}