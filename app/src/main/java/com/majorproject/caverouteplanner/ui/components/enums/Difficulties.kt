package com.majorproject.caverouteplanner.ui.components.enums

/**
 * This enum is used to represent the difficulty of a cave
 */
enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    MIXED("Mixed"),
    NONE("None")
    ;
    companion object {
        /**
         * Returns a list of all difficulties except NONE - since NONE is not a difficulty and should not be displayed
         */
        fun all(): List<Difficulty>{
            return Difficulty.entries.filter { it != NONE }
        }
    }
}