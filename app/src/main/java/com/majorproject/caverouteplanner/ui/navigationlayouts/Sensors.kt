package com.majorproject.caverouteplanner.ui.navigationlayouts

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.round

class SensorActivity(val context: Context,
    val sensorReading: (Double) -> Unit = {},) : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private val rotationVectorReading = FloatArray(5)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)


    init {
        sensorSetup()
    }

    fun sensorSetup() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.also { rotationVector ->
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun updateOrientationAngles() {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVectorReading)

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuthRadians = orientationAngles[0]

        val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble())

        val normalizedAzimuth = (azimuthDegrees + 360.0) % 360.0

        val angle = round(normalizedAzimuth * 100) / 100

        sensorReading(angle)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        System.arraycopy(event.values, 0, rotationVectorReading, 0, rotationVectorReading.size)


        updateOrientationAngles()
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}
