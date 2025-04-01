package com.majorproject.caverouteplanner.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.majorproject.caverouteplanner.datasource.util.ListConverter
import com.majorproject.caverouteplanner.datasource.util.PairConverter
import com.majorproject.caverouteplanner.model.daos.CaveDao
import com.majorproject.caverouteplanner.model.daos.SurveyDao
import com.majorproject.caverouteplanner.model.daos.SurveyNodeDao
import com.majorproject.caverouteplanner.model.daos.SurveyPathDao
import com.majorproject.caverouteplanner.ui.components.Cave
import com.majorproject.caverouteplanner.ui.components.SurveyEntity
import com.majorproject.caverouteplanner.ui.components.SurveyNodeEntity
import com.majorproject.caverouteplanner.ui.components.SurveyPathEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SurveyEntity::class, SurveyNodeEntity::class, SurveyPathEntity::class, Cave::class],
    version = 1)
@TypeConverters(ListConverter::class, PairConverter::class)
abstract class CaveRoutePlannerRoomDatabase: RoomDatabase() {
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

        private suspend fun populateDatabase(context: Context, db: CaveRoutePlannerRoomDatabase) {
            val surveyDao = db.surveyDao()
            val surveyNodeDao = db.surveyNodeDao()
            val surveyPathDao = db.surveyPathDao()
            val caveDao = db.caveDao()

            val cave = Cave(
                name = "Llygadlchwr",
                description = "cool",
                difficulty = "easy",
                location = "Llygadlchwr",
            )

            caveDao.insertCave(cave)

            val survey = SurveyEntity(
                width = 1991,
                height = 1429,
                pixelsPerMeter = 14.600609f,
                imageReference = "R.drawable.llygadlchwr",
                caveId = cave.id
            )

            //surveyDao.insertSurvey(survey)

            val surveyNode = SurveyNodeEntity(
                isEntrance = true,
                isJunction = true,
                x = 368,
                y = 85,
                edges = listOf(0),
                surveyId = survey.id
            )

            val surveyPath = SurveyPathEntity(
                ends = Pair(368, 85),
                distance = 0.0f,
                hasWater = false,
                altitude = 0,
                isHardTraverse = false,
                surveyId = survey.id
            )

            //surveyNodeDao.insertSurveyNode(surveyNode)
            //surveyPathDao.insertSurveyPath(surveyPath)

        }

    }



}