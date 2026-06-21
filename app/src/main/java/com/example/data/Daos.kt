package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthMetricDao {
    @Query("SELECT * FROM growth_metric ORDER BY date ASC")
    fun getAllMetrics(): Flow<List<GrowthMetric>>

    @Query("SELECT * FROM growth_metric WHERE platform = :platform ORDER BY date ASC")
    fun getMetricsByPlatform(platform: String): Flow<List<GrowthMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: GrowthMetric)

    @Delete
    suspend fun deleteMetric(metric: GrowthMetric)

    @Query("DELETE FROM growth_metric WHERE id = :id")
    suspend fun deleteMetricById(id: Int)
}

@Dao
interface ViralChallengeDao {
    @Query("SELECT * FROM viral_challenge")
    fun getAllChallenges(): Flow<List<ViralChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ViralChallenge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ViralChallenge>)

    @Query("UPDATE viral_challenge SET completedDays = :completedDays, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateProgress(id: String, completedDays: Int, lastUpdated: Long)

    @Query("UPDATE viral_challenge SET isAccepted = :isAccepted, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateAcceptance(id: String, isAccepted: Boolean, lastUpdated: Long)
}

@Dao
interface ContentScheduleDao {
    @Query("SELECT * FROM content_schedule ORDER BY scheduledTime ASC")
    fun getAllSchedules(): Flow<List<ContentSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ContentSchedule)

    @Query("UPDATE content_schedule SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("DELETE FROM content_schedule WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)
}

@Dao
interface CompletedTipDao {
    @Query("SELECT * FROM completed_tip")
    fun getAllCompletedTips(): Flow<List<CompletedTip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markTipCompleted(completedTip: CompletedTip)

    @Query("DELETE FROM completed_tip WHERE tipId = :tipId")
    suspend fun unmarkTipCompleted(tipId: Int)
}

@Dao
interface AchievementBadgeDao {
    @Query("SELECT * FROM achievement_badge")
    fun getAllBadges(): Flow<List<AchievementBadge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<AchievementBadge>)

    @Query("UPDATE achievement_badge SET isUnlocked = 1, unlockedTime = :unlockedTime WHERE id = :id")
    suspend fun unlockBadge(id: String, unlockedTime: Long)
}
