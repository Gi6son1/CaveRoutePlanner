package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.round

fun calculateFractionalOffset(tapLoc: Offset, size: IntSize): Offset {
    return Offset(
        x = (tapLoc.x / size.width.toFloat()),
        y = (tapLoc.y / size.height.toFloat())
    )
}

fun calculateAngle(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>): Double {
    val angle = Math.toDegrees(
        atan2(
            (coord2.second - coord1.second).toDouble(),
            (coord2.first - coord1.first).toDouble()
        )
    )

    return angle
}

fun calculatePathLength(start: Pair<Int, Int>, end: Pair<Int, Int>, pixelsPerMeter: Float): Float {
    val xDiff = (end.first - start.first).toFloat()
    val yDiff = (end.second - start.second).toFloat()
    return kotlin.math.sqrt(xDiff * xDiff + yDiff * yDiff) / pixelsPerMeter
}

fun calculateCoordinatePixels(coords: Offset, size: IntSize): Offset {
    return Offset(
        x = (coords.x * size.width.toFloat()),
        y = (coords.y * size.height.toFloat())
    )
}

fun calculatePixelsPerMeter(coords1: Offset, coords2: Offset, size: IntSize, distance: Int): Float{
    val xDiff = (coords2.x - coords1.x) * size.width
    val yDiff = (coords2.y - coords1.y) * size.height

    return kotlin.math.sqrt(xDiff.pow(2) + yDiff.pow(2)) / distance
}

fun calculateMetersFromFractionalOffsets(coords1: Offset, coords2: Offset, size: IntSize, pixelsPerMeter: Float): Int {
    val xDiff = (coords2.x - coords1.x) * size.width
    val yDiff = (coords2.y - coords1.y) * size.height

    return round(kotlin.math.sqrt(xDiff.pow(2) + yDiff.pow(2)) / pixelsPerMeter).toInt()
}

fun calculateDistance(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>) =
    kotlin.math.sqrt((coord2.second - coord1.second).toFloat().pow(2) + (coord2.first - coord1.first).toFloat().pow(2))