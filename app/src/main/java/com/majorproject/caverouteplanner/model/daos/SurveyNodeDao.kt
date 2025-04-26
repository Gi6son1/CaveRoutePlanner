package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import kotlinx.coroutines.flow.Flow

/**
 * This interface holds the database queries for the survey nodes
 */
@Dao
interface SurveyNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveyNode(surveyNode: SurveyNode): Long //this returns a long, which is the new id of the inserted node in the database

    @Query("SELECT * FROM surveyNodes WHERE surveyId = :surveyId")
    fun getSurveyNodesBySurveyId(surveyId: Int): Flow<List<SurveyNode>>

    @Query("SELECT * FROM surveyNodes WHERE id = :nodeId")
    fun getSurveyNodeById(nodeId: Int) : Flow<SurveyNode?>
}