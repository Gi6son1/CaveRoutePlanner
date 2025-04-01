package com.majorproject.caverouteplanner.datasource

import android.app.Application

class CaveRoutePlannerRepository(application: Application) {
    private val caveDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.caveDao()
    private val surveyDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyDao()
    private val surveyNodeDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyNodeDao()
    private val surveyPathDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyPathDao()

    fun getCaveById(caveId: Int) = caveDao.getCave(caveId)
    fun getAllCaves() = caveDao.getAllCaves()

    fun getSurveyById(surveyId: Int) = surveyDao.getSurveyById(surveyId)
    fun getAllSurveys() = surveyDao.getAllSurveys()

    fun getSurveyNodesBySurveyId(surveyId: Int) = surveyNodeDao.getSurveyNodesBySurveyId(surveyId)

    fun getSurveyPathsBySurveyId(surveyId: Int) = surveyPathDao.getSurveyPathsBySurveyId(surveyId)

}