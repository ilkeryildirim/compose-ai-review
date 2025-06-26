package com.github.ilkeryildirim.aireviewcompose.core

/**
 * Supported AI providers
 */
enum class AIProviderType(
    val displayName: String,
    val description: String
) {
    OPENAI("OpenAI", "ChatGPT and GPT-4 models"),
    GOOGLE("Google Gemini", "Gemini Pro and Gemini Flash"),
    HUGGINGFACE("HuggingFace", "Open source models");
    
    val supportedModels: List<AIModel>
        get() = when (this) {
            OPENAI -> listOf(
                AIModel.GPT_3_5_TURBO,
                AIModel.GPT_4_TURBO,
                AIModel.GPT_4O_MINI,
                AIModel.GPT_4O
            )
            GOOGLE -> listOf(
                AIModel.GEMINI_1_5_FLASH,
                AIModel.GEMINI_1_5_PRO
            )
            HUGGINGFACE -> listOf(
                AIModel.LLAMA_3_1_8B,
                AIModel.MISTRAL_7B
            )
        }
}

/**
 * Supported AI models
 */
enum class AIModel(
    val modelId: String,
    val displayName: String,
    val description: String,
    val provider: AIProviderType,
    val tokensPerMinute: Int,
    val requestsPerMinute: Int,
    val costEfficiency: CostEfficiency
) {
    // OpenAI Models
    GPT_3_5_TURBO(
        modelId = "gpt-3.5-turbo",
        displayName = "GPT-3.5 Turbo",
        description = "Fast and economical, general use",
        provider = AIProviderType.OPENAI,
        tokensPerMinute = 30_000,
        requestsPerMinute = 500,
        costEfficiency = CostEfficiency.HIGH
    ),
    GPT_4_TURBO(
        modelId = "gpt-4-turbo",
        displayName = "GPT-4 Turbo",
        description = "Powerful and fast, advanced capabilities",
        provider = AIProviderType.OPENAI,
        tokensPerMinute = 40_000,
        requestsPerMinute = 500,
        costEfficiency = CostEfficiency.MEDIUM
    ),
    GPT_4O_MINI(
        modelId = "gpt-4o-mini",
        displayName = "GPT-4o Mini",
        description = "Latest mini model, optimized",
        provider = AIProviderType.OPENAI,
        tokensPerMinute = 200_000,
        requestsPerMinute = 500,
        costEfficiency = CostEfficiency.HIGH
    ),
    GPT_4O(
        modelId = "gpt-4o",
        displayName = "GPT-4o",
        description = "Most advanced model, best quality",
        provider = AIProviderType.OPENAI,
        tokensPerMinute = 30_000,
        requestsPerMinute = 500,
        costEfficiency = CostEfficiency.MEDIUM
    ),
    // Google Models - 1.5 Series
    GEMINI_1_5_FLASH(
        modelId = "gemini-1.5-flash",
        displayName = "Gemini 1.5 Flash",
        description = "Fast and efficient Google model",
        provider = AIProviderType.GOOGLE,
        tokensPerMinute = 100_000,
        requestsPerMinute = 1000,
        costEfficiency = CostEfficiency.HIGH
    ),
    GEMINI_1_5_PRO(
        modelId = "gemini-1.5-pro",
        displayName = "Gemini 1.5 Pro",
        description = "Google's most advanced model",
        provider = AIProviderType.GOOGLE,
        tokensPerMinute = 50_000,
        requestsPerMinute = 500,
        costEfficiency = CostEfficiency.MEDIUM
    ),
    
    // HuggingFace Models
    LLAMA_3_1_8B(
        modelId = "meta-llama/Llama-3.1-8B",
        displayName = "Llama 3.1 8B",
        description = "Meta's open source model",
        provider = AIProviderType.HUGGINGFACE,
        tokensPerMinute = 50_000,
        requestsPerMinute = 200,
        costEfficiency = CostEfficiency.HIGH
    ),
    MISTRAL_7B(
        modelId = "mistralai/Mistral-7B-v0.1",
        displayName = "Mistral 7B",
        description = "Europe's powerful model",
        provider = AIProviderType.HUGGINGFACE,
        tokensPerMinute = 40_000,
        requestsPerMinute = 150,
        costEfficiency = CostEfficiency.HIGH
    )
}

/**
 * Cost Efficiency
 */
enum class CostEfficiency(val displayName: String, val color: String) {
    HIGH("High", "#4CAF50"),      // Green
    MEDIUM("Medium", "#FF9800"),  // Orange  
    LOW("Low", "#F44336")         // Red
}

/**
 * AI Features
 */
enum class AIFeature(val displayName: String) {
    SENTIMENT_ANALYSIS("Sentiment Analysis"),
    KEYWORD_EXTRACTION("Keyword Extraction"),
    SUMMARY_GENERATION("Summary Generation"),
    DOMAIN_DETECTION("Domain Detection"),
    COMPREHENSIVE_ANALYSIS("Comprehensive Analysis"),
    REAL_TIME_ANALYSIS("Real-time Analysis")
}

/**
 * AI Provider Interface
 */
interface AIProvider {
    val providerType: AIProviderType
    val currentModel: AIModel
    val supportedFeatures: Set<AIFeature>
    
    suspend fun isAvailable(): Boolean
    suspend fun validateApiKey(apiKey: String): Boolean
    suspend fun initialize(apiKey: String, model: AIModel? = null)
    suspend fun switchModel(model: AIModel)
    fun configure(language: String, insightProvider: com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider? = null)
    
    suspend fun analyzeSentiment(text: String, language: String = "en"): com.github.ilkeryildirim.aireviewcompose.data.models.SentimentResult
    suspend fun extractKeywords(text: String, maxKeywords: Int = 10): List<com.github.ilkeryildirim.aireviewcompose.data.models.Keyword>
    suspend fun generateSummary(texts: List<String>): com.github.ilkeryildirim.aireviewcompose.data.models.Summary
    suspend fun analyzeComprehensive(
        reviews: List<com.github.ilkeryildirim.aireviewcompose.data.models.Review>,
        forcedDomain: com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain? = null,
        language: String = "en"
    ): com.github.ilkeryildirim.aireviewcompose.data.models.ComprehensiveAnalysis
} 