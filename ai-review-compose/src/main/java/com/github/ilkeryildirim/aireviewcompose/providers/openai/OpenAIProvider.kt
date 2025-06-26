package com.github.ilkeryildirim.aireviewcompose.providers.openai

import android.content.Context
import android.util.Log
import com.github.ilkeryildirim.aireviewcompose.core.AIFeature
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.ComprehensiveAnalyzer
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.KeywordExtractor
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.SentimentAnalyzer
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.SummaryGenerator
import com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.providers.DefaultDomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.prompts.PromptManager
import com.github.ilkeryildirim.aireviewcompose.data.models.*
import com.github.ilkeryildirim.aireviewcompose.providers.BaseProvider
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * OpenAI GPT provider implementation - Simple and clean!
 */
@Singleton
class OpenAIProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : 
    BaseProvider(),
    SentimentAnalyzer, 
    KeywordExtractor, 
    SummaryGenerator, 
    ComprehensiveAnalyzer {

    override val providerType = AIProviderType.OPENAI
    override var currentModel: AIModel = AIModel.GPT_4O_MINI
    override val supportedFeatures = setOf(
        AIFeature.SENTIMENT_ANALYSIS,
        AIFeature.KEYWORD_EXTRACTION, 
        AIFeature.SUMMARY_GENERATION,
        AIFeature.COMPREHENSIVE_ANALYSIS
    )

    private lateinit var apiKey: String
    private var insightProvider: DomainInsightProvider = DefaultDomainInsightProvider()
    private var language: String = "en"
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun isAvailable(): Boolean = ::apiKey.isInitialized

    override suspend fun validateApiKey(apiKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!apiKey.startsWith("sk-") || apiKey.length < 20) {
                    Log.d("AIReviewCompose-OpenAI", "Invalid OpenAI API key format")
                    return@withContext false
                }
                val testRequest = OpenAIRequest(
                    model = currentModel.modelId,
                    messages = listOf(
                        OpenAIMessage(role = "user", content = "Hello")
                    ),
                    temperature = 0.1f
                )

                val jsonBody = json.encodeToString(OpenAIRequest.serializer(), testRequest)
                
                val request = okhttp3.Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val isValid = response.isSuccessful
                
                Log.d("AIReviewCompose-OpenAI", "OpenAI API key validation: $isValid")
                isValid
            } catch (e: Exception) {
                Log.e("AIReviewCompose-OpenAI", "OpenAI API key validation failed: ${e.message}")
                false
            }
        }
    }

    override suspend fun initialize(apiKey: String, model: AIModel?) {
        Log.d("AIReviewCompose-OpenAI", "🔧 Initializing OpenAI Provider")
        Log.d("AIReviewCompose-OpenAI", "🔑 API Key: ${apiKey.take(8)}...")
        
        model?.let { 
            if (it.provider == AIProviderType.OPENAI) {
                currentModel = it
                Log.d("AIReviewCompose-OpenAI", "🎯 Model set to: ${it.displayName}")
            } else {
                Log.w("AIReviewCompose-OpenAI", "⚠️ Invalid model for OpenAI: ${it.displayName}")
            }
        }
        
        Log.d("AIReviewCompose-OpenAI", "🚀 Using model: ${currentModel.displayName} (${currentModel.modelId})")
        Log.d("AIReviewCompose-OpenAI", "📊 Model limits: ${currentModel.tokensPerMinute} TPM, ${currentModel.requestsPerMinute} RPM")
        
        this.apiKey = apiKey
    }

    override suspend fun switchModel(model: AIModel) {
        if (model.provider != AIProviderType.OPENAI) {
            throw IllegalArgumentException("Model ${model.displayName} is not supported by OpenAI provider")
        }
        
        Log.d("AIReviewCompose-OpenAI", "🔄 Switching model from ${currentModel.displayName} to ${model.displayName}")
        currentModel = model
        Log.d("AIReviewCompose-OpenAI", "✅ Model switched successfully")
    }
    
    /**
     * Configures the provider with language and insight provider
     */
    override fun configure(language: String, insightProvider: DomainInsightProvider?) {
        this.language = language
        insightProvider?.let { this.insightProvider = it }
        Log.d("AIReviewCompose-OpenAI", "🔧 Provider configured - Language: $language")
    }

    override suspend fun analyzeSentiment(text: String, language: String): SentimentResult {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-OpenAI", "🎭 Analyzing sentiment with ${currentModel.displayName}")
            Log.d("AIReviewCompose-OpenAI", "📝 Text: ${text.take(100)}...")
            
            val prompt = PromptManager.generateSentimentPrompt(text, language)

            Log.d("AIReviewCompose-OpenAI", "📤 Sending sentiment analysis request...")
            val response = makeOpenAIRequest(prompt)
            Log.d("AIReviewCompose-OpenAI", "📥 Received sentiment response: ${response.take(200)}...")
            
            val result = parseSentimentResponse(response)
            Log.d("AIReviewCompose-OpenAI", "✅ Sentiment analysis completed: ${result.sentiment}")
            result
        }
    }

    override suspend fun extractKeywords(text: String, maxKeywords: Int): List<Keyword> {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-OpenAI", "🔑 Extracting keywords with ${currentModel.displayName}")
            
            val prompt = PromptManager.generateKeywordPrompt(text, maxKeywords)

            val response = makeOpenAIRequest(prompt)
            parseKeywordResponse(response)
        }
    }

    override suspend fun generateSummary(texts: List<String>): Summary {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-OpenAI", "📋 Generating summary with ${currentModel.displayName}")
            
            val prompt = PromptManager.generateSummaryPrompt(texts, language)

            val response = makeOpenAIRequest(prompt)
            parseSummaryResponse(response, texts.size)
        }
    }

    override suspend fun analyzeComprehensive(
        reviews: List<Review>,
        forcedDomain: ReviewDomain?,
        language: String
    ): ComprehensiveAnalysis {
        Log.d("AIReviewCompose-OpenAI", "🚀 Starting comprehensive analysis with ${currentModel.displayName}")
        Log.d("AIReviewCompose-OpenAI", "📊 Model efficiency: ${currentModel.costEfficiency.displayName}")
        Log.d("AIReviewCompose-OpenAI", "⚡ TPM Limit: ${currentModel.tokensPerMinute}, RPM Limit: ${currentModel.requestsPerMinute}")
        
        val texts = reviews.map { it.text }
        val combinedText = texts.joinToString(" ")
        
        Log.d("AIReviewCompose-OpenAI", "📝 Combined text length: ${combinedText.length} chars")
        
        try {
            val domainResult = if (forcedDomain != null) {
                Log.d("AIReviewCompose-OpenAI", "🎯 Using forced domain: ${forcedDomain.displayName}")
                Pair(forcedDomain, 1.0f)
            } else {
                Log.d("AIReviewCompose-OpenAI", "🎯 Starting automatic domain detection...")
                detectDomain(combinedText, language)
            }
            
            Log.d("AIReviewCompose-OpenAI", "🎭 Starting domain-aware sentiment analysis...")
            val sentiment = analyzeSentimentWithDomain(combinedText, domainResult.first, language)
            
            Log.d("AIReviewCompose-OpenAI", "🔑 Starting domain-aware keyword extraction...")
            val keywords = extractKeywordsWithDomain(combinedText, domainResult.first, 10, language)
            
            Log.d("AIReviewCompose-OpenAI", "📋 Starting domain-aware summary generation...")
            val summary = generateSummaryWithDomain(texts, domainResult.first, language)
            
            val domainInsights = generateDomainInsights(combinedText, domainResult.first, language)
            
            val result = ComprehensiveAnalysis(
                sentiment = sentiment,
                keywords = keywords,
                summary = summary,
                totalReviews = reviews.size,
                overallRating = reviews.mapNotNull { it.rating }.average().takeIf { !it.isNaN() }?.toFloat(),
                detectedDomain = domainResult.first,
                domainConfidence = domainResult.second,
                domainSpecificInsights = domainInsights
            )
            
            Log.d("AIReviewCompose-OpenAI", "✅ Comprehensive analysis completed with ${currentModel.displayName}")
            Log.d("AIReviewCompose-OpenAI", "🎯 Detected domain: ${domainResult.first?.displayName} (${(domainResult.second * 100).toInt()}%)")
            return result
            
        } catch (e: Exception) {
            Log.e("AIReviewCompose-OpenAI", "❌ Comprehensive analysis failed with ${currentModel.displayName}: ${e.message}", e)
            throw e
        }
    }

    private suspend fun detectDomain(text: String, language: String = "en"): Pair<ReviewDomain?, Float> {
        return withContext(Dispatchers.IO) {
            Log.d("AIReviewCompose-OpenAI", "🔍 Detecting domain for text analysis...")
            
            val prompt = PromptManager.generateDomainDetectionPrompt(text)

            try {
                val response = makeOpenAIRequest(prompt)
                val cleanedResponse = cleanJsonResponse(response)
                val jsonResponse = json.decodeFromString<DomainDetectionResponse>(cleanedResponse)
                
                val domain = when (jsonResponse.domain.uppercase()) {
                    "E_COMMERCE" -> ReviewDomain.E_COMMERCE
                    "FOOD" -> ReviewDomain.FOOD
                    "RESTAURANT" -> ReviewDomain.RESTAURANT
                    "SERVICE" -> ReviewDomain.SERVICE
                    "APP" -> ReviewDomain.APP
                    else -> ReviewDomain.OTHER
                }
                
                Log.d("AIReviewCompose-OpenAI", "✅ Domain detected: ${domain.displayName} (${(jsonResponse.confidence * 100).toInt()}%)")
                Log.d("AIReviewCompose-OpenAI", "🔍 Reasoning: ${jsonResponse.reasoning}")
                Pair(domain, jsonResponse.confidence)
                
            } catch (e: Exception) {
                Log.w("AIReviewCompose-OpenAI", "Failed to detect domain, using OTHER", e)
                Pair(ReviewDomain.OTHER, 0.5f)
            }
        }
    }

    private suspend fun analyzeSentimentWithDomain(text: String, domain: ReviewDomain?, language: String = "en"): SentimentResult {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateSentimentPrompt(text, language, domain)
            val response = makeOpenAIRequest(prompt)
            parseSentimentResponse(response)
        }
    }

    private suspend fun extractKeywordsWithDomain(text: String, domain: ReviewDomain?, maxKeywords: Int, language: String = "en"): List<Keyword> {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateKeywordPrompt(text, maxKeywords, domain)
            val response = makeOpenAIRequest(prompt)
            parseKeywordResponse(response)
        }
    }

    private suspend fun generateSummaryWithDomain(texts: List<String>, domain: ReviewDomain?, language: String = "en"): Summary {
        return withContext(Dispatchers.IO) {
            val prompt = PromptManager.generateSummaryPrompt(texts, language, domain)
            val response = makeOpenAIRequest(prompt)
            parseSummaryResponse(response, texts.size)
        }
    }

    private fun generateDomainInsights(text: String, domain: ReviewDomain?, language: String = "en"): Map<String, String> {
        return domain?.let { 
            insightProvider.getInsights(it, language)
        } ?: insightProvider.getInsights(ReviewDomain.OTHER, language)
    }

    private suspend fun makeOpenAIRequest(prompt: String): String {
        Log.d("AIReviewCompose-OpenAI", "🔧 Preparing OpenAI request with ${currentModel.displayName}")
        
        val requestBody = OpenAIRequest(
            model = currentModel.modelId,
            messages = listOf(
                OpenAIMessage(
                    role = "system",
                    content = "You are a review analysis assistant. Return ONLY valid JSON without any markdown formatting, code blocks, or additional text. Do not wrap JSON in ```json blocks."
                ),
                OpenAIMessage(
                    role = "user",
                    content = prompt
                )
            ),
            temperature = 0.1f
        )

        val jsonBody = json.encodeToString(OpenAIRequest.serializer(), requestBody)
        Log.d("AIReviewCompose-OpenAI", "📦 Request body size: ${jsonBody.length} chars")
        Log.d("AIReviewCompose-OpenAI", "🎯 Target model: ${currentModel.modelId}")
        
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()
        
        Log.d("AIReviewCompose-OpenAI", "🔑 Using API key: ${apiKey.take(8)}... (${apiKey.length} chars)")

        Log.d("AIReviewCompose-OpenAI", "🌐 Making HTTP request to OpenAI...")
        
        return httpClient.newCall(request).execute().use { response ->
            Log.d("AIReviewCompose-OpenAI", "📨 Received response: ${response.code}")
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e("AIReviewCompose-OpenAI", "❌ OpenAI API error: ${response.code} - $errorBody")
                throw Exception("OpenAI API error: ${response.code} - $errorBody")
            }
            
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            Log.d("AIReviewCompose-OpenAI", "📄 Response body size: ${responseBody.length} chars")
            
            val openAIResponse = json.decodeFromString(OpenAIResponse.serializer(), responseBody)
            val content = openAIResponse.choices.firstOrNull()?.message?.content 
                ?: throw Exception("No content in response")
                
            Log.d("AIReviewCompose-OpenAI", "✅ Successfully extracted content: ${content.take(100)}...")
            content
        }
    }

    private fun cleanJsonResponse(response: String): String {
        return response.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    private fun parseSentimentResponse(response: String): SentimentResult {
        return try {
            val cleanedResponse = cleanJsonResponse(response)
            val jsonResponse = json.decodeFromString<SentimentResponse>(cleanedResponse)
            SentimentResult(
                sentiment = when (jsonResponse.sentiment.uppercase()) {
                    "POSITIVE" -> Sentiment.POSITIVE
                    "NEGATIVE" -> Sentiment.NEGATIVE
                    "MIXED" -> Sentiment.MIXED
                    else -> Sentiment.NEUTRAL
                },
                confidence = jsonResponse.confidence,
                scores = SentimentScores(
                    positive = jsonResponse.scores.positive,
                    negative = jsonResponse.scores.negative,
                    neutral = jsonResponse.scores.neutral
                ),
                providerType = providerType.name
            )
        } catch (e: Exception) {
            Log.w("AIReviewCompose-OpenAI", "Failed to parse sentiment response, using fallback", e)
            SentimentResult(
                sentiment = Sentiment.NEUTRAL,
                confidence = 0.5f,
                scores = SentimentScores(0.33f, 0.33f, 0.34f),
                providerType = providerType.name
            )
        }
    }

    private fun parseKeywordResponse(response: String): List<Keyword> {
        return try {
            val cleanedResponse = cleanJsonResponse(response)
            val jsonResponse = json.decodeFromString<KeywordResponse>(cleanedResponse)
            jsonResponse.keywords.map { keyword ->
                Keyword(
                    word = keyword.word,
                    relevance = keyword.relevance,
                    count = keyword.count,
                    category = keyword.category
                )
            }
        } catch (e: Exception) {
            Log.w("AIReviewCompose-OpenAI", "Failed to parse keyword response", e)
            emptyList()
        }
    }

    private fun parseSummaryResponse(response: String, originalCount: Int): Summary {
        return try {
            val cleanedResponse = cleanJsonResponse(response)
            val jsonResponse = json.decodeFromString<SummaryResponse>(cleanedResponse)
            Summary(
                text = jsonResponse.text,
                originalCount = originalCount,
                confidence = jsonResponse.confidence,
                highlights = jsonResponse.highlights
            )
        } catch (e: Exception) {
            Log.w("AIReviewCompose-OpenAI", "Failed to parse summary response", e)
            Summary(
                text = "Analysis could not be completed.",
                originalCount = originalCount,
                confidence = 0.0f,
                highlights = emptyList()
            )
        }
    }
}

@Serializable
private data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    val temperature: Float
)

@Serializable
private data class OpenAIMessage(
    val role: String,
    val content: String
)

@Serializable
private data class OpenAIResponse(
    val choices: List<OpenAIChoice>
)

@Serializable
private data class OpenAIChoice(
    val message: OpenAIMessage
)

@Serializable
private data class SentimentResponse(
    val sentiment: String,
    val confidence: Float,
    val scores: SentimentScoresResponse
)

@Serializable
private data class SentimentScoresResponse(
    val positive: Float,
    val negative: Float,
    val neutral: Float
)

@Serializable
private data class KeywordResponse(
    val keywords: List<KeywordItem>
)

@Serializable
private data class KeywordItem(
    val word: String,
    val relevance: Float,
    val count: Int,
    val category: String
)

@Serializable
private data class SummaryResponse(
    val text: String,
    val confidence: Float,
    val highlights: List<String>
)

@Serializable
private data class DomainDetectionResponse(
    val domain: String,
    val confidence: Float,
    val reasoning: String
) 