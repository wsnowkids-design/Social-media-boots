package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = LocalRepository(db)

    // SharedPreferences for User Settings
    private val prefs = application.getSharedPreferences("social_boost_prefs", Context.MODE_PRIVATE)

    // User Profile state
    private val _username = MutableStateFlow(prefs.getString("username", "yourname") ?: "yourname")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _niche = MutableStateFlow(prefs.getString("niche", "Tech & Gadgets") ?: "Tech & Gadgets")
    val niche: StateFlow<String> = _niche.asStateFlow()

    private val _primaryPlatform = MutableStateFlow(prefs.getString("primary_platform", "Instagram") ?: "Instagram")
    val primaryPlatform: StateFlow<String> = _primaryPlatform.asStateFlow()

    // Connection State flows for Social Profile Verification System
    private val _connectedInstagram = MutableStateFlow(prefs.getBoolean("conn_instagram", false))
    val connectedInstagram: StateFlow<Boolean> = _connectedInstagram.asStateFlow()
    private val _instagramEmail = MutableStateFlow(prefs.getString("conn_instagram_email", "") ?: "")
    val instagramEmail: StateFlow<String> = _instagramEmail.asStateFlow()
    private val _instagramPhone = MutableStateFlow(prefs.getString("conn_instagram_phone", "") ?: "")
    val instagramPhone: StateFlow<String> = _instagramPhone.asStateFlow()

    private val _connectedTikTok = MutableStateFlow(prefs.getBoolean("conn_tiktok", false))
    val connectedTikTok: StateFlow<Boolean> = _connectedTikTok.asStateFlow()
    private val _tiktokEmail = MutableStateFlow(prefs.getString("conn_tiktok_email", "") ?: "")
    val tiktokEmail: StateFlow<String> = _tiktokEmail.asStateFlow()
    private val _tiktokPhone = MutableStateFlow(prefs.getString("conn_tiktok_phone", "") ?: "")
    val tiktokPhone: StateFlow<String> = _tiktokPhone.asStateFlow()

    private val _connectedYouTube = MutableStateFlow(prefs.getBoolean("conn_youtube", false))
    val connectedYouTube: StateFlow<Boolean> = _connectedYouTube.asStateFlow()
    private val _youtubeEmail = MutableStateFlow(prefs.getString("conn_youtube_email", "") ?: "")
    val youtubeEmail: StateFlow<String> = _youtubeEmail.asStateFlow()
    private val _youtubePhone = MutableStateFlow(prefs.getString("conn_youtube_phone", "") ?: "")
    val youtubePhone: StateFlow<String> = _youtubePhone.asStateFlow()

    private val _connectedFacebook = MutableStateFlow(prefs.getBoolean("conn_facebook", false))
    val connectedFacebook: StateFlow<Boolean> = _connectedFacebook.asStateFlow()
    private val _facebookEmail = MutableStateFlow(prefs.getString("conn_facebook_email", "") ?: "")
    val facebookEmail: StateFlow<String> = _facebookEmail.asStateFlow()
    private val _facebookPhone = MutableStateFlow(prefs.getString("conn_facebook_phone", "") ?: "")
    val facebookPhone: StateFlow<String> = _facebookPhone.asStateFlow()

    // Screen navigation
    private val _currentTab = MutableStateFlow("Dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // AI Generation States
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    // Calculated engagement
    private val _calcResult = MutableStateFlow<EngagementResult?>(null)
    val calcResult: StateFlow<EngagementResult?> = _calcResult.asStateFlow()

    // Flows from database
    val metrics: StateFlow<List<GrowthMetric>> = repository.allMetrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challenges: StateFlow<List<ViralChallenge>> = repository.allChallenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<ContentSchedule>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTipsList: StateFlow<List<CompletedTip>> = repository.completedTips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<AchievementBadge>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkAndInitializeDefaults()
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // Update Profile settings
    fun updateProfile(newUsername: String, newNiche: String, newPlatform: String) {
        prefs.edit().apply {
            putString("username", newUsername)
            putString("niche", newNiche)
            putString("primary_platform", newPlatform)
            apply()
        }
        _username.value = newUsername
        _niche.value = newNiche
        _primaryPlatform.value = newPlatform

        // Unlock welcome badge
        viewModelScope.launch {
            repository.unlockBadge("badge_welcome")
        }
    }

    // Connect Social Media profiles with Email, Phone Number, and OTP Verification
    fun connectSocialProfile(platform: String, email: String, phone: String) {
        val platformKey = platform.lowercase()
        prefs.edit().apply {
            putBoolean("conn_$platformKey", true)
            putString("conn_${platformKey}_email", email)
            putString("conn_${platformKey}_phone", phone)
            apply()
        }
        when (platform) {
            "Instagram" -> {
                _connectedInstagram.value = true
                _instagramEmail.value = email
                _instagramPhone.value = phone
            }
            "TikTok" -> {
                _connectedTikTok.value = true
                _tiktokEmail.value = email
                _tiktokPhone.value = phone
            }
            "YouTube" -> {
                _connectedYouTube.value = true
                _youtubeEmail.value = email
                _youtubePhone.value = phone
            }
            "Facebook" -> {
                _connectedFacebook.value = true
                _facebookEmail.value = email
                _facebookPhone.value = phone
            }
        }
    }

    // Disconnect linked profiles securely
    fun disconnectSocialProfile(platform: String) {
        val platformKey = platform.lowercase()
        prefs.edit().apply {
            putBoolean("conn_$platformKey", false)
            putString("conn_${platformKey}_email", "")
            putString("conn_${platformKey}_phone", "")
            apply()
        }
        when (platform) {
            "Instagram" -> {
                _connectedInstagram.value = false
                _instagramEmail.value = ""
                _instagramPhone.value = ""
            }
            "TikTok" -> {
                _connectedTikTok.value = false
                _tiktokEmail.value = ""
                _tiktokPhone.value = ""
            }
            "YouTube" -> {
                _connectedYouTube.value = false
                _youtubeEmail.value = ""
                _youtubePhone.value = ""
            }
            "Facebook" -> {
                _connectedFacebook.value = false
                _facebookEmail.value = ""
                _facebookPhone.value = ""
            }
        }
    }

    // AI Trigger
    fun generateAIContent(platform: String, nicheInput: String, topic: String, type: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            _aiResponse.value = null
            try {
                val result = GeminiClient.generateGrowthMaterials(platform, nicheInput, topic, type)
                _aiResponse.value = result
                // Unlock AI brain badge!
                repository.unlockBadge("badge_ai_ideas")
            } catch (e: Exception) {
                _aiResponse.value = "An error occurred: ${e.message}"
            } finally {
                _aiLoading.value = false
            }
        }
    }

    fun clearAIResponse() {
        _aiResponse.value = null
    }

    // Engagement Calculator Utility
    fun calculateEngagement(followersStr: String, likesStr: String, commentsStr: String, sharesStr: String) {
        val followers = followersStr.toDoubleOrNull() ?: 0.0
        val likes = likesStr.toDoubleOrNull() ?: 0.0
        val comments = commentsStr.toDoubleOrNull() ?: 0.0
        val shares = sharesStr.toDoubleOrNull() ?: 0.0

        if (followers <= 0) {
            _calcResult.value = EngagementResult(0.00, "Too few followers to compute. Double-check your numbers!", "N/A")
            return
        }

        val engagementPoints = likes + (comments * 2.0) + (shares * 3.0) // commenting & sharing weighs extra!
        val ratePercent = (engagementPoints / followers) * 100.0
        val rateFormatted = Math.round(ratePercent * 100.0) / 100.0

        val (rating, advice) = when {
            rateFormatted < 1.5 -> Pair("Below Average", "Try adding interactive 'ask me anything' captions and response stickers to encourage micro-actions.")
            rateFormatted < 3.0 -> Pair("Average Organics", "A solid baseline! Boost this by answering 100% of comments within the first 60 minutes.")
            rateFormatted < 6.0 -> Pair("Good Standing", "Very healthy! Your community is active. Integrate daily consistency challenges to amplify outreach.")
            else -> Pair("Exceptional Viral Potential", "Outstanding! The algorithm loves your hook density. Keep uploading Reels and TikTok loops regularly!")
        }

        _calcResult.value = EngagementResult(rateFormatted, advice, rating)

        viewModelScope.launch {
            repository.unlockBadge("badge_calc")
        }
    }

    // Tracking Metrics
    fun logMetric(platform: String, followers: Long, likes: Long, er: Double, posts: Int) {
        viewModelScope.launch {
            val metric = GrowthMetric(
                date = System.currentTimeMillis(),
                platform = platform,
                followers = followers,
                likes = likes,
                engagementRate = er,
                postsCount = posts
            )
            repository.insertMetric(metric)
        }
    }

    fun deleteMetric(metric: GrowthMetric) {
        viewModelScope.launch {
            repository.deleteMetric(metric)
        }
    }

    // Challenges Management
    fun acceptChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.acceptChallenge(challengeId)
        }
    }

    fun incrementChallenge(challengeId: String, currentVal: Int, totalVal: Int) {
        viewModelScope.launch {
            repository.incrementChallenge(challengeId, currentVal, totalVal)
        }
    }

    fun resetChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.resetChallenge(challengeId)
        }
    }

    // Content Calendar Actions
    fun createSchedule(title: String, caption: String, platform: String, time: Long, type: String) {
        viewModelScope.launch {
            val item = ContentSchedule(
                title = title,
                caption = caption,
                platform = platform,
                scheduledTime = time,
                status = "Scheduled",
                type = type
            )
            repository.insertSchedule(item)
        }
    }

    fun updateScheduleStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateScheduleStatus(id, status)
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            repository.deleteSchedule(id)
        }
    }

    // Completing Tips
    fun toggleTipCompleted(tipId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                repository.unmarkTipRead(tipId)
            } else {
                repository.markTipRead(tipId)
            }
        }
    }

    // Computed total followers across logged platforms (the most recent logged log per platform)
    val statsSummary: StateFlow<StatsSummary> = metrics.map { list ->
        val latestByPlatform = list.groupBy { it.platform }.mapValues { entry ->
            entry.value.maxByOrNull { it.date }
        }

        val totalFollowers = latestByPlatform.values.filterNotNull().sumOf { it.followers }
        val totalLikes = latestByPlatform.values.filterNotNull().sumOf { it.likes }
        val countLogPlatforms = latestByPlatform.size

        StatsSummary(totalFollowers, totalLikes, countLogPlatforms, latestByPlatform)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary())

    // Simulated Global Lead list with the user's handle placed dynamically
    val leaderboard: StateFlow<List<LeaderboardUser>> = statsSummary.map { summary ->
        val userFollowers = summary.totalFollowers
        val baseUsers = mutableListOf(
            LeaderboardUser("@viral_vance", 480000, "TikTok", 1),
            LeaderboardUser("@chef_delight", 180000, "Instagram", 2),
            LeaderboardUser("@beauty_guru", 120000, "YouTube", 3),
            LeaderboardUser("@fit_journey", 45000, "Instagram", 4),
            LeaderboardUser("@travel_bounds", 15000, "Facebook", 5)
        )

        val userObj = LeaderboardUser("@${username.value}", userFollowers, primaryPlatform.value, 6, isCurrentUser = true)
        baseUsers.add(userObj)

        // Sort descending
        baseUsers.sortByDescending { it.followers }

        // Assign Rank indices
        baseUsers.mapIndexed { idx, item ->
            item.copy(rank = idx + 1)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

data class EngagementResult(
    val rate: Double,
    val advice: String,
    val rating: String
)

data class StatsSummary(
    val totalFollowers: Long = 0L,
    val totalLikes: Long = 0L,
    val loggedCount: Int = 0,
    val latestLogs: Map<String, GrowthMetric?> = emptyMap()
)

data class LeaderboardUser(
    val username: String,
    val followers: Long,
    val primaryPlatform: String,
    val rank: Int,
    val isCurrentUser: Boolean = false
)
