package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        GrowthMetric::class,
        ViralChallenge::class,
        ContentSchedule::class,
        CompletedTip::class,
        AchievementBadge::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun growthMetricDao(): GrowthMetricDao
    abstract fun viralChallengeDao(): ViralChallengeDao
    abstract fun contentScheduleDao(): ContentScheduleDao
    abstract fun completedTipDao(): CompletedTipDao
    abstract fun achievementBadgeDao(): AchievementBadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "social_boost_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
