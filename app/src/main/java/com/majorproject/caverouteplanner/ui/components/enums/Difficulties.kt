package com.majorproject.caverouteplanner.ui.components.enums

enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    MIXED("Mixed"),
    NONE("None")
    ;
    companion object {
        fun all(): List<Difficulty>{
            return Difficulty.entries.filter { it != NONE }
        }
    }
}