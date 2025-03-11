package com.majorproject.caverouteplanner.ui.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyNode(
    var id: Int,
    var isEntrance: Boolean = false,
    var isJunction: Boolean = false,
    var coordinates: Pair<Int, Int>,
    var edges: MutableList<Int>
) : Parcelable