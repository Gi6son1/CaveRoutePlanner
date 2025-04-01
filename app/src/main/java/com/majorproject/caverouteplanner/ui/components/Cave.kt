package com.majorproject.caverouteplanner.ui.components

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "caves")
data class Cave(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val difficulty: String,
    val location: String,
)