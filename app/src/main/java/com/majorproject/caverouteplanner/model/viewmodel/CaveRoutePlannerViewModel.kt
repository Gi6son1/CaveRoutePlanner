package com.majorproject.caverouteplanner.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CaveRoutePlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CaveRoutePlannerRepository(application)

    val caveList: StateFlow<List<Cave>> = repository.getAllCaves().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val surveyList: StateFlow<List<Survey>> = repository.getAllSurveysWithData().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun saveNewCave(caveProperties: CaveProperties, surveyProps: SurveyProperties, surveyNodes: List<SurveyNode>, surveyPaths: List<SurveyPath>){
            repository.saveCaveAndSurvey(caveProperties, surveyProps, surveyNodes, surveyPaths)
    }

}