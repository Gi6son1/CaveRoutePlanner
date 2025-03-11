package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Parcelised so that it can be stored in remember saveables
@Parcelize
data class SurveyPath(
    var id: Int,
    var ends: Pair<Int, Int>,
    var distance: Float = 0.0f,
    var hasWater: Boolean = false,
    var altitude: Int = 0,
    var isHardTraverse: Boolean = false
) : Parcelable