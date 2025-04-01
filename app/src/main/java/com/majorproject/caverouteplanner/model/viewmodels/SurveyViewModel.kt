package com.majorproject.caverouteplanner.model.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.majorproject.caverouteplanner.datasource.CaveRoutePlannerRepository

class SurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CaveRoutePlannerRepository(application)

//    fun getSurveyById(surveyId: Int): SurveyWithNodesAndEdges? {
//            return repository.getSurveyById(surveyId)
//    }
}