package com.majorproject.caverouteplanner.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.majorproject.caverouteplanner.datasource.util.PairConverter
import com.majorproject.caverouteplanner.model.daos.CaveDao
import com.majorproject.caverouteplanner.model.daos.SurveyDao
import com.majorproject.caverouteplanner.model.daos.SurveyNodeDao
import com.majorproject.caverouteplanner.model.daos.SurveyPathDao
import com.majorproject.caverouteplanner.ui.components.CaveProperties
import com.majorproject.caverouteplanner.ui.components.SurveyNode
import com.majorproject.caverouteplanner.ui.components.SurveyPath
import com.majorproject.caverouteplanner.ui.components.SurveyProperties
import com.majorproject.caverouteplanner.ui.components.llSurveyReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SurveyProperties::class, SurveyNode::class, SurveyPath::class, CaveProperties::class],
    version = 1
)
@TypeConverters(PairConverter::class)
abstract class CaveRoutePlannerRoomDatabase : RoomDatabase() {
    abstract fun surveyDao(): SurveyDao
    abstract fun surveyNodeDao(): SurveyNodeDao
    abstract fun surveyPathDao(): SurveyPathDao
    abstract fun caveDao(): CaveDao

    companion object {
        private var instance: CaveRoutePlannerRoomDatabase? = null
        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        @Synchronized
        fun getDatabase(context: Context): CaveRoutePlannerRoomDatabase? {
            if (instance == null) {
                instance =
                    Room.databaseBuilder<CaveRoutePlannerRoomDatabase>(
                        context.applicationContext,
                        CaveRoutePlannerRoomDatabase::class.java,
                        "caverouteplanner_database"
                    )
                        .allowMainThreadQueries()
                        .addCallback(roomDatabaseCallback(context))
                        .build()
            }
            return instance
        }


        private fun roomDatabaseCallback(context: Context): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("CaveRoutePlannerRoomDatabase", "onCreate")

                    coroutineScope.launch {
                        populateDatabase(context, getDatabase(context)!!)
                    }
                }
            }
        }

        private fun populateDatabase(context: Context, db: CaveRoutePlannerRoomDatabase) {
            val surveyDao = db.surveyDao()
            val surveyNodeDao = db.surveyNodeDao()
            val surveyPathDao = db.surveyPathDao()
            val caveDao = db.caveDao()

            val llSurveyReference = llSurveyReference
            Log.d("TimeSTAMP", "in populate database")


            val llSurveyProps = SurveyProperties(
                width = 1991,
                height = 1429,
                pixelsPerMeter = 14.600609f,
                imageReference = llSurveyReference.imageReference,
                northAngle = llSurveyReference.northAngle
            )

            val llSurveyId = surveyDao.insertSurvey(llSurveyProps)

            val llCaveProperties = CaveProperties(
                name = "Llygadlchwr",
                description = "Contains dry high level and an active river level, separated by sumps.",
                difficulty = "Novice",
                location = "South Wales",
                length = 1.2f,
                surveyId = llSurveyId.toInt()
            )

            caveDao.insertCaveProperties(llCaveProperties)

            var nodesList: MutableList<Int> =
                MutableList(llSurveyReference.pathNodes.size) { _ -> 0 }

            for (node in llSurveyReference.pathNodes) {
                val nodeDBID = surveyNodeDao.insertSurveyNode(
                    SurveyNode(
                        isEntrance = node.isEntrance,
                        isJunction = node.isJunction,
                        x = node.coordinates.first,
                        y = node.coordinates.second,
                        surveyId = llSurveyId.toInt()
                    )
                )
                nodesList[node.id] = nodeDBID.toInt()
            }

            for (path in llSurveyReference.paths) {
                val ends = Pair(nodesList[path.ends.first], nodesList[path.ends.second])
                surveyPathDao.insertSurveyPath(
                    SurveyPath(
                        ends = ends,
                        distance = path.distance,
                        hasWater = path.hasWater,
                        altitude = path.altitude,
                        isHardTraverse = path.isHardTraverse,
                        surveyId = llSurveyId.toInt()
                    )
                )
            }
        }
    }
}