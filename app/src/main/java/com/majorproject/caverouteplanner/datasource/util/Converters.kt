package com.majorproject.caverouteplanner.datasource.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * This file contains the converters used in the database, converting from DB format to Kotlin objects and vice versa
 */


/**
 * This class is used for converting a list of integers to a string and vice versa
 *
 * It was originally used when a node has a list of edges it was part of, but this was removed
 */
class ListConverter {
    @TypeConverter
    fun fromString(listString: String): MutableList<Int> {
        val listType = object : TypeToken<MutableList<Int>>() {}.type
        return Gson().fromJson(listString, listType)
    }

    @TypeConverter
    fun toString(list: MutableList<Int>): String {
        return Gson().toJson(list)
    }
}

/**
 * This class is used for converting a pair of integers to a string and vice versa
 *
 * It's used when a survey path's pari of ends has to be converted in the DB
 */
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