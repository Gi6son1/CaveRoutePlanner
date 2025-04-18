package com.majorproject.caverouteplanner.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.Cave
import kotlinx.coroutines.flow.Flow

@Dao
interface CaveDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCaveProperties(caveProperties: CaveProperties)

    @Query("SELECT * FROM caves WHERE name = :caveId")
    fun getCaveProperties(caveId: Int): CaveProperties?

    @Query("SELECT * FROM caves")
    fun getAllCaves(): Flow<List<Cave>>

    @Transaction
    @Query("SELECT * FROM caves WHERE id = :caveId")
    fun getCaveById(caveId: Int): Flow<Cave?>
}