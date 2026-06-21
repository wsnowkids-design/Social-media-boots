package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateGrowthMaterials(
        platform: String,
        niche: String,
        promptTopic: String,
        type: String // "Caption", "Ideas", "Plan"
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return "API Key is missing or using placeholder in config. Please configure GEMINI_API_KEY in the Secrets panel or .env file."
        }

        val systemInstructionText = """
            You are "Social Boost AI", an elite growth copywriter and organic growth strategist for $platform.
            Your response must be extremely professional, visually structured using beautiful bullet points and clear sections, and formatted strictly for $platform's current algorithm trends. 
            Do NOT include unnecessary filler. Get straight to actionable advice.
        """.trimIndent()

        val prompt = when (type) {
            "Caption" -> """
                Generate 3 high-converting post caption variations for $platform in the $niche niche.
                The topic is: "$promptTopic".
                Each variation must include:
                - An elite attention hook (First line)
                - High-value body context
                - Call to action (CTA) to encourage comments or shares
                - 10 targeted hashtags optimized for $platform organic search index. Make them copy-paste friendly.
            """.trimIndent()
            "Ideas" -> """
                Generate 5 viral, organic content ideas for $platform in the $niche niche.
                The core concept is: "$promptTopic".
                For each idea, provide:
                1. Concept Title
                2. Exact Video Hook (what to say/show in the first 3 seconds)
                3. Content Outline / Script beats
                4. Production Difficulty (Easy, Medium, Hard)
            """.trimIndent()
            else -> """
                Create a 3-day organic content publication strategy for $platform in the $niche niche centering on "$promptTopic".
                Provide:
                - Day 1, Day 2, Day 3 post formats (Reel, Story, Link post, etc.)
                - Suggested Posting Time (based on organic traffic peak stats)
                - Detailed step-by-step visual & dialogue plan
                - Relevant copyable hook and hashtag tags.
            """.trimIndent()
        }

        val request = GeminiRequest(
            contents = listOf(
                MoshiContent(parts = listOf(MoshiPart(text = prompt)))
            ),
            systemInstruction = MoshiContent(parts = listOf(MoshiPart(text = systemInstructionText))),
            generationConfig = MoshiGenerationConfig(
                temperature = 0.7f,
                maxOutputTokens = 2048
            )
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No suggestions returned. Please try again with different keywords or topic tags."
        } catch (e: Exception) {
            "Error: Failed to connect to Social Boost AI. Please check your network connection or API Key. Details: ${e.message}"
        }
    }
}
