package com.majorproject.caverouteplanner.datasource

import android.app.Application
import android.util.Log
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.llSurveyReference

class CaveRoutePlannerRepository(application: Application) {
    private val caveDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.caveDao()
    private val surveyDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyDao()
    private val surveyNodeDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyNodeDao()
    private val surveyPathDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyPathDao()

    fun getCavePropertiesById(caveId: Int) = caveDao.getCaveProperties(caveId)
    fun getAllCaves() = caveDao.getAllCaves()

    fun getSurveyById(surveyId: Int) = surveyDao.getSurveyById(surveyId)
    fun getAllSurveys() = surveyDao.getAllSurveys()
    fun getAllSurveysWithData() = surveyDao.getAllSurveysWithData()

    fun getSurveyNodesBySurveyId(surveyId: Int) = surveyNodeDao.getSurveyNodesBySurveyId(surveyId)

    fun getSurveyPathsBySurveyId(surveyId: Int) = surveyPathDao.getSurveyPathsBySurveyId(surveyId)

    fun getCave(caveId: Int) = caveDao.getCaveById(caveId)

    fun getSurveyWithDataById(surveyId: Int) = surveyDao.getSurveyWithDataById(surveyId)

    fun saveCaveAndSurvey(caveProperties: CaveProperties, surveyProps: SurveyProperties, surveyNodes: List<SurveyNode>, surveyPaths: List<SurveyPath>) {
        val surveyId = surveyDao.insertSurvey(surveyProps)

        val finalisedCaveProperties = CaveProperties(
            name = caveProperties.name,
            description = caveProperties.description,
            difficulty = caveProperties.difficulty,
            location = caveProperties.location,
            length = caveProperties.length,
            surveyId = surveyId.toInt()
        )

        caveDao.insertCaveProperties(finalisedCaveProperties)

        var nodesList: MutableList<Int> =
            MutableList(surveyNodes.size) { _ -> 0 }

        for (node in surveyNodes) {
            val nodeDBID = surveyNodeDao.insertSurveyNode(
                SurveyNode(
                    isEntrance = node.isEntrance,
                    isJunction = node.isJunction,
                    x = node.x,
                    y = node.y,
                    surveyId = surveyId.toInt()
                )
            )
            nodesList[node.id] = nodeDBID.toInt()
        }

        for (path in surveyPaths) {
            val ends = Pair(nodesList[path.ends.first], nodesList[path.ends.second])
            surveyPathDao.insertSurveyPath(
                SurveyPath(
                    ends = ends,
                    distance = path.distance,
                    hasWater = path.hasWater,
                    altitude = path.altitude,
                    isHardTraverse = path.isHardTraverse,
                    surveyId = surveyId.toInt()
                )
            )
        }
    }

}