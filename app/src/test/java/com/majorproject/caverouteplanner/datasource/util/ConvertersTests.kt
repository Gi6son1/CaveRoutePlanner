package com.majorproject.caverouteplanner.datasource.util

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ListConvertersTests{

    val listConverter = ListConverter()

    val inputLists = listOf(
        mutableListOf(1,2,3),
        mutableListOf(),
        mutableListOf(-9)
    )

    val expectedOutputs = listOf(
        "[1,2,3]",
        "[]",
        "[-9]"
    )

    @Test
    fun intToStringConverterTest(){
        for(i in inputLists.indices){
            val output = listConverter.toString(inputLists[i])
            assertEquals(expectedOutputs[i], output)
        }
    }

    @Test
    fun stringToIntConverterTest(){
        for (i in inputLists.indices){
            val output = listConverter.fromString(expectedOutputs[i])
            assertEquals(inputLists[i], output)
        }
    }
}

class PairConvertersTests{

    val pairConverter = PairConverter()

    val inputPairs = listOf(
        Pair(1,2),
        Pair(0,0),
        Pair(-1,-2)
    )

    val expectedOutputs = listOf(
        "{\"first\":1,\"second\":2}",
        "{\"first\":0,\"second\":0}",
        "{\"first\":-1,\"second\":-2}"
    )

    @Test
    fun pairToStringConverterTest() {
        for (i in inputPairs.indices) {
            val output = pairConverter.toString(inputPairs[i])
            assertEquals(expectedOutputs[i], output)
        }
    }

    @Test
    fun stringToPairConverterTest() {
        for (i in inputPairs.indices) {
            val output = pairConverter.fromString(expectedOutputs[i])
            assertEquals(inputPairs[i], output)
        }
    }
}