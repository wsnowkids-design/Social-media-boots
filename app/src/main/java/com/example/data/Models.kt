package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "growth_metric")
data class GrowthMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long, // timestamp
    val platform: String, // "Instagram", "Facebook", "TikTok", "YouTube"
    val followers: Long,
    val likes: Long,
    val engagementRate: Double,
    val postsCount: Int
)

@Entity(tableName = "viral_challenge")
data class ViralChallenge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val platform: String, // "Instagram", "Facebook", "TikTok", "YouTube", "All"
    val totalDays: Int,
    val completedDays: Int,
    val isAccepted: Boolean,
    val lastUpdated: Long
)

@Entity(tableName = "content_schedule")
data class ContentSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val caption: String,
    val platform: String, // "Instagram", "Facebook", "TikTok", "YouTube"
    val scheduledTime: Long, // timestamp
    val status: String, // "Draft", "Scheduled", "Published"
    val type: String // "Reel", "Post", "Video", "Story"
)

@Entity(tableName = "completed_tip")
data class CompletedTip(
    @PrimaryKey val tipId: Int,
    val completedTime: Long
)

@Entity(tableName = "achievement_badge")
data class AchievementBadge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String, // name of material icon
    val isUnlocked: Boolean,
    val unlockedTime: Long
)
