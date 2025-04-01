package com.majorproject.caverouteplanner.model.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository
import com.majorproject.caverouteplanner.ui.components.SurveyWithNodesAndEdges

class SurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CaveRoutePlannerRepository(application)

//    fun getSurveyById(surveyId: Int): SurveyWithNodesAndEdges? {
//            return repository.getSurveyById(surveyId)
//    }
}