package com.majorproject.caverouteplanner.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * This file holds the basic maths calculations that are used in the app
 */

/**
 * Function to calculate the fractional offset of a tap location on a canvas
 * @param tapLoc The location of the tap
 * @param size The size of the box for reference
 * @return The fractional offset of the tap
 */
fun calculateFractionalOffset(tapLoc: Offset, size: IntSize): Offset {
    return Offset(
        x = (tapLoc.x / size.width.toFloat()),
        y = (tapLoc.y / size.height.toFloat())
    )
}

/**
 * Function to calculate the angle between two points
 * @param coord1 The first point
 * @param coord2 The second point
 * @return The angle between the two points
 */
fun calculateAngle(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>): Double {
    val angle = Math.toDegrees(
        atan2(
            (coord2.second - coord1.second).toDouble(),
            (coord2.first - coord1.first).toDouble()
        )
    )
    return angle
}

/**
 * Function to calculate the pixel coordinates of a tap location on a canvas
 * @param coords The fractional offset of the tap
 * @param size The size of the box for reference
 * @return The coordinates of the tap
 */
fun calculateCoordinatePixels(coords: Offset, size: IntSize): Offset {
    return Offset(
        x = (coords.x * size.width.toFloat()),
        y = (coords.y * size.height.toFloat())
    )
}

/**
 * Function to calculate the pixels per meter of a distance in meters
 * @param coords1 The first point
 * @param coords2 The second point
 * @param size The size of the box for reference
 * @param distance The distance in meters
 */
fun calculatePixelsPerMeter(coords1: Offset, coords2: Offset, size: IntSize, distance: Int): Float{
    val xDiff = (coords2.x - coords1.x) * size.width
    val yDiff = (coords2.y - coords1.y) * size.height

    return sqrt(xDiff.pow(2) + yDiff.pow(2)) / distance
}

/**
 * Function to calculate the distance between two points in meters
 * @param coords1 The first point
 * @param coords2 The second point
 * @param size The size of the box for reference
 * @param pixelsPerMeter The pixels per meter for reference
 * @return The distance between the two points in meters
 */
fun calculateMetersFromFractionalOffsets(coords1: Offset, coords2: Offset, size: IntSize, pixelsPerMeter: Float): Int {
    val xDiff = (coords2.x - coords1.x) * size.width
    val yDiff = (coords2.y - coords1.y) * size.height

    val diff = sqrt(xDiff.pow(2) + yDiff.pow(2))
    return round (diff /  pixelsPerMeter) .toInt()
}

/**
 * Function to calculate the distance between two points in pixels
 * @param coord1 The first point
 * @param coord2 The second point
 * @return The distance between the two points in pixels
 */
fun calculateDistance(coord1: Pair<Int, Int>, coord2: Pair<Int, Int>) =
    sqrt((coord2.second - coord1.second).toFloat().pow(2) + (coord2.first - coord1.first).toFloat().pow(2))