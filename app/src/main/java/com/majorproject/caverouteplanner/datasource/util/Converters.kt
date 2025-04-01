package com.majorproject.caverouteplanner.datasource.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
    @TypeConverter
    fun fromString(listString: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(listString, listType)
    }

    @TypeConverter
    fun toString(list: List<Int>): String {
        return Gson().toJson(list)
    }
}

class PairConverter {
    @TypeConverter
    fun fromString(pairString: String): Pair<Int, Int> {
        val pairType = object : TypeToken<Pair<Int, Int>>() {}.type
        return Gson().fromJson(pairString, pairType)
    }

    @TypeConverter
    fun toString(pair: Pair<Int, Int>): String {
        return Gson().toJson(pair)
    }
}