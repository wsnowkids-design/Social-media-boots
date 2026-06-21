package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
data class GrowthTip(
    val id: Int,
    val title: String,
    val platform: String, // "Instagram", "Facebook", "TikTok", "YouTube", "All"
    val category: String, // "Hooks", "SEO", "Engage", "Algorithm"
    val summary: String,
    val actionSteps: String,
    val difficulty: String // "Beginner", "Intermediate", "Advanced"
)

class LocalRepository(private val db: AppDatabase) {

    val allMetrics: Flow<List<GrowthMetric>> = db.growthMetricDao().getAllMetrics()
    val allChallenges: Flow<List<ViralChallenge>> = db.viralChallengeDao().getAllChallenges()
    val allSchedules: Flow<List<ContentSchedule>> = db.contentScheduleDao().getAllSchedules()
    val completedTips: Flow<List<CompletedTip>> = db.completedTipDao().getAllCompletedTips()
    val allBadges: Flow<List<AchievementBadge>> = db.achievementBadgeDao().getAllBadges()

    // Base mock tips that are immediately visible in the dashboard
    val growthTips = listOf(
        GrowthTip(
            id = 1,
            title = "The 3-Second Hook Rule",
            platform = "TikTok",
            category = "Hooks",
            summary = "Capture attention instantly before viewers scroll. Use bold text on screen, move fast, and don't start with a greeting.",
            actionSteps = "1. Cut the first 'Hey guys!' from your video.\n2. Write a polarizing or curious question on-screen.\n3. Keep the first scene under 1.5 seconds.",
            difficulty = "Beginner"
        ),
        GrowthTip(
            id = 2,
            title = "Optimizing Shorts for Loops",
            platform = "YouTube",
            category = "Algorithm",
            summary = "YouTube Shorts reward high watch-time. Create seamless loops where the end of the video is indistinguishable from the beginning.",
            actionSteps = "1. End your sentence with a conjunction (e.g., 'and that's why...').\n2. Start the video with the resolution (e.g., '...this is the best tool!').\n3. Match the background music sound level globally.",
            difficulty = "Intermediate"
        ),
        GrowthTip(
            id = 3,
            title = "Instagram Carousel Save-Magnets",
            platform = "Instagram",
            category = "SEO",
            summary = "The Instagram algorithm boosts posts that are bookmarked. Create detailed step-by-step guides that people want to save for reference later.",
            actionSteps = "1. Frame slide 1 as: 'How to X in 3 simple steps (Save for later)'.\n2. Make slides 2-4 bite-sized and actionable.\n3. Make slide 5 a summary cheat sheet summarizing the entire carousel.",
            difficulty = "Beginner"
        ),
        GrowthTip(
            id = 4,
            title = "Organic FB Reels Spark",
            platform = "Facebook",
            category = "Algorithm",
            summary = "Facebook is aggressively recommending Reels to non-followers. Share micro-stories with direct, relatable emotional overlays.",
            actionSteps = "1. Record an everyday activity (e.g., coding, coffee making, walking).\n2. Superimpose a text block showing a hard truth about your niche.\n3. Keep descriptions descriptive and relatable.",
            difficulty = "Beginner"
        ),
        GrowthTip(
            id = 5,
            title = "SEO-Driven Description Optimization",
            platform = "YouTube",
            category = "SEO",
            summary = "Search Optimization is the highest source of long-tail organic YouTube views. Use rich keyword-optimized clusters in your first 3 script lines.",
            actionSteps = "1. Research 3 long-tail search terms related to your topic.\n2. Explicitly pronounce these keywords in your audio.\n3. Place those terms naturally in the first 200 characters of your video description.",
            difficulty = "Advanced"
        ),
        GrowthTip(
            id = 6,
            title = "Consistent Dialogue Engagement",
            platform = "Instagram",
            category = "Engage",
            summary = "Boost comments significantly by putting a specific conversation starter callback in your active caption rather than generic 'What do you think?'.",
            actionSteps = "1. Ask a choice-based question: 'Are you Team A or Team B? Comment index below!'.\n2. Manually reply to every single comment within the first 60 minutes of posting.\n3. Initiate secondary conversational questions in your feedback replies.",
            difficulty = "Intermediate"
        ),
        GrowthTip(
            id = 7,
            title = "Trending Sounds Overlay Trick",
            platform = "TikTok",
            category = "Algorithm",
            summary = "Ride trendy wave audio tracks even when giving spoken audio. Superimpose trending sounds but set their volume level to 3% to preserve your speech.",
            actionSteps = "1. Open TikTok editor, choose a track from 'Trending'.\n2. Add your spoken video.\n3. Decrease the background audio volume slider down to 1-3% so it acts as quiet atmospheric floor hum.",
            difficulty = "Intermediate"
        ),
        GrowthTip(
            id = 8,
            title = "Microblogging FB Post Power",
            platform = "Facebook",
            category = "Engage",
            summary = "Clean, text-only posts on Facebook with a colored background still generate highly intimate reach compared to externally linked clickbaity posts.",
            actionSteps = "1. Write a short, highly debatable question under 130 characters.\n2. Apply a vibrant custom solid background color (e.g., deep slate or gradient slate).\n3. Avoid adding any links in the post context (drop them in the comment section instead).",
            difficulty = "Intermediate"
        )
    )

    // Initial setup to run once on first startup
    suspend fun checkAndInitializeDefaults() {
        // Challenges
        val currentChallenges = db.viralChallengeDao().getAllChallenges().firstOrNull() ?: emptyList()
        if (currentChallenges.isEmpty()) {
            val defaults = listOf(
                ViralChallenge(
                    id = "reels_consistency",
                    title = "7-Day Consistent Reels",
                    description = "Publish 1 Reel daily for 7 days focusing strictly on dynamic hooks within the first 3 seconds.",
                    platform = "Instagram",
                    totalDays = 7,
                    completedDays = 0,
                    isAccepted = false,
                    lastUpdated = 0L
                ),
                ViralChallenge(
                    id = "tiktok_hook_master",
                    title = "5-Day TikTok Hook Boost",
                    description = "Film short-form videos with bold, on-screen text overlays matching daily viral formats to boost average watch length.",
                    platform = "TikTok",
                    totalDays = 5,
                    completedDays = 0,
                    isAccepted = false,
                    lastUpdated = 0L
                ),
                ViralChallenge(
                    id = "yt_shorts_spike",
                    title = "10-Day Shorts Marathon",
                    description = "Post a 15-second YouTube Short daily at your audience's high-traffic peak hours focusing on visual loops.",
                    platform = "YouTube",
                    totalDays = 10,
                    completedDays = 0,
                    isAccepted = false,
                    lastUpdated = 0L
                ),
                ViralChallenge(
                    id = "fb_community_organic",
                    title = "5-Day Conversation Sparker",
                    description = "Post a high-value discussion piece in target Facebook group clusters to organic brand-authoritative awareness.",
                    platform = "Facebook",
                    totalDays = 5,
                    completedDays = 0,
                    isAccepted = false,
                    lastUpdated = 0L
                )
            )
            db.viralChallengeDao().insertChallenges(defaults)
        }

        // Badges
        val currentBadges = db.achievementBadgeDao().getAllBadges().firstOrNull() ?: emptyList()
        if (currentBadges.isEmpty()) {
            val defaults = listOf(
                AchievementBadge("badge_welcome", "Growth Kickstarter", "Configured profile niche & joined Social Boost.", "rocket_launch", true, System.currentTimeMillis()),
                AchievementBadge("badge_ai_ideas", "AI Brainstormer", "Generated caption draft or viral ideas with Social Boost AI.", "psychology", false, 0L),
                AchievementBadge("badge_stats_logged", "Metric Strategist", "Logged weekly analytics or follower changes manually in growth tracker.", "show_chart", false, 0L),
                AchievementBadge("badge_challenge", "Challenger Accepted", "Accepted an intensive organic daily viral growth challenge.", "emoji_events", false, 0L),
                AchievementBadge("badge_calc", "Audience Inspector", "Evaluated channels performance via the engagement rate calculator.", "calculate", false, 0L),
                AchievementBadge("badge_cal_item", "Master Planner", "Scheduled a planned post in your integrated Content Calendar.", "calendar_month", false, 0L)
            )
            db.achievementBadgeDao().insertBadges(defaults)
        }
    }

    // Helper functions
    suspend fun insertMetric(metric: GrowthMetric) {
        db.growthMetricDao().insertMetric(metric)
        unlockBadge("badge_stats_logged")
    }

    suspend fun deleteMetric(metric: GrowthMetric) {
        db.growthMetricDao().deleteMetric(metric)
    }

    suspend fun acceptChallenge(id: String) {
        db.viralChallengeDao().updateAcceptance(id, true, System.currentTimeMillis())
        unlockBadge("badge_challenge")
    }

    suspend fun incrementChallenge(id: String, currentComp: Int, maxWeeks: Int) {
        val newComp = (currentComp + 1).coerceAtMost(maxWeeks)
        db.viralChallengeDao().updateProgress(id, newComp, System.currentTimeMillis())
    }

    suspend fun resetChallenge(id: String) {
        db.viralChallengeDao().updateProgress(id, 0, System.currentTimeMillis())
        db.viralChallengeDao().updateAcceptance(id, false, System.currentTimeMillis())
    }

    suspend fun insertSchedule(schedule: ContentSchedule) {
        db.contentScheduleDao().insertSchedule(schedule)
        unlockBadge("badge_cal_item")
    }

    suspend fun deleteSchedule(id: Int) {
        db.contentScheduleDao().deleteScheduleById(id)
    }

    suspend fun updateScheduleStatus(id: Int, status: String) {
        db.contentScheduleDao().updateStatus(id, status)
    }

    suspend fun markTipRead(tipId: Int) {
        db.completedTipDao().markTipCompleted(CompletedTip(tipId, System.currentTimeMillis()))
    }

    suspend fun unmarkTipRead(tipId: Int) {
        db.completedTipDao().unmarkTipCompleted(tipId)
    }

    suspend fun unlockBadge(badgeId: String) {
        db.achievementBadgeDao().unlockBadge(badgeId, System.currentTimeMillis())
    }
}
