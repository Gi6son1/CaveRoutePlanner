package com.majorproject.caverouteplanner.datasource

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.round

/**
 * This class holds the sensor management for the application
 *
 * It is used to give the compass data to the survey navigation screen
 *
 * @param context The context of the activity
 * @param sensorReading A function that returns the compass data to the calling function
 */
class SensorActivity(
    val context: Context,
    val sensorReading: (Double) -> Unit = {},
) : SensorEventListener {
    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val rotationVectorReading = FloatArray(5)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    /**
     * This function starts the sensor
     */
    fun start() {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
    }

    /**
     * This function stops the sensor, conserving battery when needed
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /**
     * This function retrieves the sensor data, converts it into a current compass direction and returns it to the calling function
     */
    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVectorReading)

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuthRadians = orientationAngles[0]

        val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble())

        val normalizedAzimuth = (azimuthDegrees + 360.0) % 360.0

        val angle = round(normalizedAzimuth * 100) / 100

        sensorReading(angle)
    }

    /**
     * This function is called when the sensor data changes, when it does, the sensor data is updated and the current compass direction is calculated
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        System.arraycopy(event.values, 0, rotationVectorReading, 0, rotationVectorReading.size)


        updateOrientationAngles()
    }


    /**
     * This function is called when the sensor accuracy changes, but is not used in this project, due to this still being an experimental feature
     *
     * Once implemented, it would be used to adjust the accuracy of the sensor and maybe allow some re-calibration
     */
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}
