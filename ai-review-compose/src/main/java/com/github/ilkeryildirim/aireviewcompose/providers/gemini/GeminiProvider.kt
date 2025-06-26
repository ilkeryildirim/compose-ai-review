package com.github.ilkeryildirim.aireviewcompose.providers.gemini

import android.util.Log
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProvider
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType
import com.github.ilkeryildirim.aireviewcompose.core.AIFeature
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.ComprehensiveAnalyzer
import com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.providers.DefaultDomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.prompts.PromptManager
import com.github.ilkeryildirim.aireviewcompose.data.models.*
import com.github.ilkeryildirim.aireviewcompose.providers.BaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google AI Gemini provider implementation - Simple and clean!
 */
@Singleton
class GeminiProvider @Inject constructor() : BaseProvider(), AIProvider, ComprehensiveAnalyzer {

    override val providerType: AIProviderType = AIProviderType.GOOGLE
    override val supportedFeatures: Set<AIFeature> = setOf(
        AIFeature.SENTIMENT_ANALYSIS,
        AIFeature.KEYWORD_EXTRACTION,
        AIFeature.SUMMARY_GENERATION,
        AIFeature.DOMAIN_DETECTION
    )

    private var apiKey: String = ""
    override var currentModel: AIModel = AIModel.GEMINI_1_5_FLASH
    private var language: String = "en"
    private var insightProvider: DomainInsightProvider = DefaultDomainInsightProvider()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun isAvailable(): Boolean = true

    override suspend fun validateApiKey(apiKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Gemini API keys typically start with "AIza" and have minimum length
                if (!apiKey.startsWith("AIza") && apiKey.length < 20) {
                    Log.d("AIReviewCompose-Gemini", "Invalid Gemini API key format")
                    return@withContext false
                }
                
                val testRequest = """
                    {
                        "contents": [{
                            "parts": [{"text": "Hello"}]
                        }]
                    }
                """.trimIndent()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/${currentModel.modelId}:generateContent?key=$apiKey")
                    .post(testRequest.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val result = response.isSuccessful
                
                Log.d("AIReviewCompose-Gemini", "Gemini API key validation: $result")
                result
            } catch (e: Exception) {
                Log.e("AIReviewCompose-Gemini", "Gemini API key validation failed: ${e.message}")
                false
            }
        }
    }

    override suspend fun initialize(apiKey: String, model: AIModel?) {
        Log.d("AIReviewCompose-Gemini", "🚀 Initializing Gemini Provider")
        Log.d("AIReviewCompose-Gemini", "🔑 API Key: ${apiKey.take(8)}...")
        Log.d("AIReviewCompose-Gemini", "🤖 Model: ${model?.displayName ?: currentModel.displayName}")

        if (apiKey.isBlank()) {
            throw IllegalArgumentException("API key cannot be empty")
        }

        model?.let { 
            if (it.provider == AIProviderType.GOOGLE) {
                currentModel = it
            } else {
                throw IllegalArgumentException("Model ${it.displayName} is not supported by Gemini provider")
            }
        }

        val isValid = validateApiKey(apiKey)
        if (!isValid) {
            throw IllegalStateException("Invalid API key or Gemini service unavailable")
        }

        this.apiKey = apiKey
        Log.d("AIReviewCompose-Gemini", "✅ Gemini Provider initialized successfully")
    }

    override suspend fun switchModel(model: AIModel) {
        if (model.provider != AIProviderType.GOOGLE) {
            throw IllegalArgumentException("Model ${model.displayName} is not supported by Gemini provider")
        }
        
        Log.d("AIReviewCompose-Gemini", "🔄 Switching to model: ${model.displayName}")
        currentModel = model
        Log.d("AIReviewCompose-Gemini", "✅ Model switched successfully")
    }

    override fun configure(language: String, insightProvider: DomainInsightProvider?) {
        this.language = language
        insightProvider?.let { this.insightProvider = it }
        Log.d("AIReviewCompose-Gemini", "⚙️ Provider configured with language: $language")
    }

    override suspend fun analyzeSentiment(text: String, language: String): SentimentResult {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-Gemini", "🎭 Analyzing sentiment with ${currentModel.displayName}")
            
            val prompt = PromptManager.generateSentimentPrompt(text, language)
            val response = makeGeminiRequest(prompt)
            
            parseSentimentResponse(response)
        }
    }

    override suspend fun extractKeywords(text: String, maxKeywords: Int): List<Keyword> {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-Gemini", "🔑 Extracting keywords with ${currentModel.displayName}")
            
            val prompt = PromptManager.generateKeywordPrompt(text, maxKeywords)
            val response = makeGeminiRequest(prompt)
            
            parseKeywordResponse(response)
        }
    }

    override suspend fun generateSummary(texts: List<String>): Summary {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-Gemini", "📋 Generating summary with ${currentModel.displayName}")
            
            val prompt = PromptManager.generateSummaryPrompt(texts, language)
            val response = makeGeminiRequest(prompt)
            
            parseSummaryResponse(response, texts.size)
        }
    }

    override suspend fun analyzeComprehensive(
        reviews: List<Review>,
        forcedDomain: ReviewDomain?,
        language: String
    ): ComprehensiveAnalysis {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-Gemini", "🔍 Starting comprehensive analysis")
            Log.d("AIReviewCompose-Gemini", "📊 Reviews: ${reviews.size}")
            Log.d("AIReviewCompose-Gemini", "🎯 Forced domain: ${forcedDomain?.displayName ?: "Auto-detect"}")

            val combinedText = reviews.joinToString(" ") { it.text }

            // Domain detection (if not forced)
            val detectedDomain = forcedDomain ?: detectDomain(combinedText)
            Log.d("AIReviewCompose-Gemini", "🏷️ Using domain: ${detectedDomain.displayName}")

            // Parallel analysis
            val sentiment = analyzeSentimentWithDomain(combinedText, detectedDomain, language)
            val keywords = extractKeywordsWithDomain(combinedText, detectedDomain, 15, language)
            val summary = generateSummaryWithDomain(reviews.map { it.text }, detectedDomain, language)

            ComprehensiveAnalysis(
                sentiment = sentiment,
                keywords = keywords,
                summary = summary,
                totalReviews = reviews.size,
                overallRating = reviews.mapNotNull { it.rating }.average().takeIf { !it.isNaN() }?.toFloat(),
                detectedDomain = detectedDomain
            )
        }
    }

    private suspend fun detectDomain(text: String): ReviewDomain {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-Gemini", "🔍 Detecting domain for text analysis...")
            
            val prompt = PromptManager.generateDomainDetectionPrompt(text)

            try {
                val response = makeGeminiRequest(prompt)
                parseDomainResponse(response)
            } catch (e: Exception) {
                Log.e("AIReviewCompose-Gemini", "Domain detection failed: ${e.message}")
                ReviewDomain.OTHER
            }
        }
    }

    private suspend fun analyzeSentimentWithDomain(text: String, domain: ReviewDomain?, language: String = "en"): SentimentResult {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateSentimentPrompt(text, language, domain)
            val response = makeGeminiRequest(prompt)
            parseSentimentResponse(response)
        }
    }

    private suspend fun extractKeywordsWithDomain(text: String, domain: ReviewDomain?, maxKeywords: Int, language: String = "en"): List<Keyword> {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateKeywordPrompt(text, maxKeywords, domain)
            val response = makeGeminiRequest(prompt)
            parseKeywordResponse(response)
        }
    }

    private suspend fun generateSummaryWithDomain(texts: List<String>, domain: ReviewDomain?, language: String = "en"): Summary {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateSummaryPrompt(texts, language, domain)
            val response = makeGeminiRequest(prompt)
            parseSummaryResponse(response, texts.size)
        }
    }

    private suspend fun makeGeminiRequest(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )

                val jsonBody = json.encodeToString(GeminiRequest.serializer(), requestBody)
                
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/${currentModel.modelId}:generateContent?key=$apiKey")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                Log.d("AIReviewCompose-Gemini", "📤 Sending request to: ${currentModel.modelId}")

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e("AIReviewCompose-Gemini", "❌ API Error: ${response.code} - $responseBody")
                    throw Exception("Gemini API error: ${response.code}")
                }

                val geminiResponse = json.decodeFromString(GeminiResponse.serializer(), responseBody)
                val generatedText = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response from Gemini")

                Log.d("AIReviewCompose-Gemini", "📥 Response received: ${generatedText.take(100)}...")
                generatedText

            } catch (e: Exception) {
                Log.e("AIReviewCompose-Gemini", "Request failed: ${e.message}", e)
                throw e
            }
        }
    }

    // Parsing methods (similar to OpenAI provider)
    private fun parseSentimentResponse(response: String): SentimentResult {
        return try {
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonStr = response.substring(jsonStart, jsonEnd)
                json.decodeFromString(SentimentResult.serializer(), jsonStr)
            } else {
                throw Exception("No valid JSON found in response")
            }
        } catch (e: Exception) {
            Log.w("AIReviewCompose-Gemini", "Failed to parse sentiment response: ${e.message}")
            SentimentResult(
                sentiment = Sentiment.NEUTRAL,
                confidence = 0.5f,
                scores = SentimentScores(
                    positive = 0.33f,
                    negative = 0.33f,
                    neutral = 0.33f,
                    mixed = 0.01f
                )
            )
        }
    }

    private fun parseKeywordResponse(response: String): List<Keyword> {
        return try {
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonStr = response.substring(jsonStart, jsonEnd)
                val keywordResponse = json.decodeFromString(KeywordResponse.serializer(), jsonStr)
                keywordResponse.keywords
            } else {
                throw Exception("No valid JSON found in response")
            }
        } catch (e: Exception) {
            Log.w("AIReviewCompose-Gemini", "Failed to parse keyword response: ${e.message}")
            emptyList()
        }
    }

    private fun parseSummaryResponse(response: String, originalCount: Int): Summary {
        return try {
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonStr = response.substring(jsonStart, jsonEnd)
                val summaryResponse = json.decodeFromString(SummaryResponse.serializer(), jsonStr)
                Summary(
                    text = summaryResponse.text,
                    confidence = summaryResponse.confidence,
                    originalCount = originalCount,
                    highlights = summaryResponse.highlights
                )
            } else {
                throw Exception("No valid JSON found in response")
            }
        } catch (e: Exception) {
            Log.w("AIReviewCompose-Gemini", "Failed to parse summary response: ${e.message}")
            Summary(
                text = "Analysis completed with ${originalCount} reviews",
                confidence = 0.5f,
                originalCount = originalCount,
                highlights = emptyList()
            )
        }
    }

    private fun parseDomainResponse(response: String): ReviewDomain {
        return try {
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonStr = response.substring(jsonStart, jsonEnd)
                val domainResponse = json.decodeFromString(DomainResponse.serializer(), jsonStr)
                ReviewDomain.valueOf(domainResponse.domain)
            } else {
                throw Exception("No valid JSON found in response")
            }
        } catch (e: Exception) {
            Log.w("AIReviewCompose-Gemini", "Failed to parse domain response: ${e.message}")
            ReviewDomain.OTHER
        }
    }
}

// Gemini API data classes
@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
private data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiPart(
    val text: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@Serializable
private data class GeminiCandidate(
    val content: GeminiContent?
)

// Response data classes
@Serializable
private data class KeywordResponse(
    val keywords: List<Keyword>
)

@Serializable
private data class SummaryResponse(
    val text: String,
    val confidence: Float,
    val highlights: List<String>
)

@Serializable
private data class DomainResponse(
    val domain: String,
    val confidence: Float,
    val reasoning: String
)