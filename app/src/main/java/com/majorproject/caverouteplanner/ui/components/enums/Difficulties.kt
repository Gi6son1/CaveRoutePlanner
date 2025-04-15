package com.majorproject.caverouteplanner.ui.components.enums

enum class Difficulty {
    EASY, MEDIUM, HARD, MIXED, NONE;
    companion object {
        fun all(): List<Difficulty>{
            return Difficulty.entries.filter { it != NONE }
        }
    }
}