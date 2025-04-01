package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.CaveWithSurvey

@Dao
interface CaveDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCave(caveProperties: CaveProperties)

    @Query("SELECT * FROM caves WHERE name = :caveId")
    fun getCave(caveId: Int): CaveProperties?

    @Query("SELECT * FROM caves")
    fun getAllCaves(): List<CaveProperties>

    @Transaction
    @Query("SELECT * FROM caves WHERE id = :caveId")
    fun getCaveWithSurveyProps(caveId: Int): CaveWithSurvey?
}