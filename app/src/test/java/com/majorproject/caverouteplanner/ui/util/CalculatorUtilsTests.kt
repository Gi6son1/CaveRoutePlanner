package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CalculatorUtilsTests {

    @Test
    fun calculateFractionalOffset_validTap_returnsCorrectOffset() {
        val tapLoc = Offset(50f, 100f)
        val size = IntSize(200, 400)
        val expected = Offset(0.25f, 0.25f)
        val result = calculateFractionalOffset(tapLoc, size)
        assertEquals(expected.x, result.x)
        assertEquals(expected.y, result.y)
    }

    @Test
    fun calculateAngle_validPoints_returnsCorrectAngle() {
        val coord1 = Pair(0, 0)
        val coord2 = Pair(1, 1)
        val result = calculateAngle(coord1, coord2)
        assertEquals(45.0, result)
    }

    @Test
    fun calculateCoordinatePixels_validFractionalOffset_returnsCorrectPixels() {
        val coords = Offset(0.5f, 0.25f)
        val size = IntSize(200, 400)
        val expected = Offset(100f, 100f)
        val result = calculateCoordinatePixels(coords, size)
        assertEquals(expected.x, result.x)
        assertEquals(expected.y, result.y)
    }

    @Test
    fun calculatePixelsPerMeter_validData_returnsCorrectPixelsPerMeter() {
        val coords1 = Offset(0f, 0f)
        val coords2 = Offset(0.5f, 0.5f)
        val size = IntSize(100, 100)
        val distance = 5
        val expected = 14.142136f
        val result = calculatePixelsPerMeter(coords1, coords2, size, distance)
        assertEquals(expected, result)
    }

    @Test
    fun calculateMetersFromFractionalOffsets_validData_returnsCorrectMeters() {
        val coords1 = Offset(0f, 0f)
        val coords2 = Offset(0.5f, 0.5f)
        val size = IntSize(100, 100)
        val pixelsPerMeter = 14.142136f
        val expected = 5
        val result = calculateMetersFromFractionalOffsets(coords1, coords2, size, pixelsPerMeter)
        assertEquals(expected, result)
    }

    @Test
    fun calculateDistance_validPoints_returnsCorrectDistance() {
        val coord1 = Pair(0, 0)
        val coord2 = Pair(3, 4)
        val result = calculateDistance(coord1, coord2)
        assertEquals(5.0f, result)
    }
}