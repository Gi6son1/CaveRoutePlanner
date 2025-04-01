package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorproject.caverouteplanner.ui.components.Cave

@Dao
interface CaveDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCave(cave: Cave)

    @Query("SELECT * FROM caves WHERE name = :caveId")
    fun getCave(caveId: Int): Cave?

    @Query("SELECT * FROM caves")
    fun getAllCaves(): List<Cave>

}