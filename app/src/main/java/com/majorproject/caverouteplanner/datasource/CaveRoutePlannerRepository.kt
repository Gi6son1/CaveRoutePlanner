package com.majorproject.caverouteplanner.datasource

import android.app.Application
import android.util.Log
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.Survey
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import kotlinx.coroutines.flow.Flow

class CaveRoutePlannerRepository(application: Application) {
    private val caveDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.caveDao()
    private val surveyDao = CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyDao()
    private val surveyNodeDao =
        CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyNodeDao()
    private val surveyPathDao =
        CaveRoutePlannerRoomDatabase.getDatabase(application)!!.surveyPathDao()

    fun getCavePropertiesById(caveId: Int) = caveDao.getCaveProperties(caveId)
    fun getAllCaves(): Flow<List<Cave>> = caveDao.getAllCaves()

    fun getSurveyById(surveyId: Int) = surveyDao.getSurveyById(surveyId)
    fun getAllSurveys() = surveyDao.getAllSurveys()
    fun getAllSurveysWithData(): Flow<List<Survey>> = surveyDao.getAllSurveysWithData()

    fun getSurveyNodesBySurveyId(surveyId: Int) = surveyNodeDao.getSurveyNodesBySurveyId(surveyId)

    fun getSurveyPathsBySurveyId(surveyId: Int) = surveyPathDao.getSurveyPathsBySurveyId(surveyId)

    fun getCave(caveId: Int) = caveDao.getCaveById(caveId)

    fun getSurveyWithDataById(surveyId: Int) = surveyDao.getSurveyWithDataById(surveyId)

    fun saveCaveAndSurvey(
        caveProperties: CaveProperties,
        surveyProps: SurveyProperties,
        surveyNodes: List<SurveyNode>,
        surveyPaths: List<SurveyPath>
    ) {
        val surveyId = surveyDao.insertSurvey(surveyProps)
        Log.d("DEBUGLOG", "Saved Survey")

        val finalisedCaveProperties = CaveProperties(
            name = caveProperties.name,
            description = caveProperties.description,
            difficulty = caveProperties.difficulty,
            location = caveProperties.location,
            length = caveProperties.length,
            surveyId = surveyId.toInt()
        )
        Log.d("DEBUGLOG", "Created CaveProperties")

        caveDao.insertCaveProperties(finalisedCaveProperties)
        Log.d("DEBUGLOG", "Saved Cave")

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
            Log.d("DEBUGLOG", "Saved Node $nodeDBID")
        }
        Log.d("DEBUGLOG", "Saved Nodes")

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