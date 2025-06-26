package com.github.ilkeryildirim.aireviewcompose

import android.content.Context
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProvider
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType
import com.github.ilkeryildirim.aireviewcompose.core.ProviderFactory
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.ComprehensiveAnalyzer
import com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.providers.DefaultDomainInsightProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Review Compose library main class with simple provider support
 */
@Singleton
class AIReviewCompose @Inject constructor(
    @ApplicationContext private val context: Context,
    private val providerFactory: ProviderFactory
) {
    
    private var isInitialized = false
    private var currentProvider: AIProvider? = null
    private var currentProviderType: AIProviderType? = null
    private var apiKey: String = ""
    private var currentModel: AIModel? = null
    private var language: String = "en"
    private var insightProvider: DomainInsightProvider = DefaultDomainInsightProvider()
    
    /**
     * Initializes AI provider with specific provider type
     * @param providerType AI provider type (OpenAI, Google, etc.)
     * @param apiKey Provider API key
     * @param model AI model to use (optional)
     * @param language Language code for responses (defaults to system language)
     * @param customInsightProvider Custom insight provider (optional)
     */
    suspend fun initialize(
        providerType: AIProviderType,
        apiKey: String, 
        model: AIModel? = null,
        language: String? = null,
        customInsightProvider: DomainInsightProvider? = null
    ) {
        android.util.Log.d("AIReviewCompose-Init", "🚀 Starting initialization with ${providerType.displayName}")
        android.util.Log.d("AIReviewCompose-Init", "🔑 API Key length: ${apiKey.length}")
        android.util.Log.d("AIReviewCompose-Init", "🎯 Selected model: ${model?.displayName ?: "Default"}")
        
        if (apiKey.isBlank()) {
            android.util.Log.e("AIReviewCompose-Init", "❌ API key is blank!")
            throw IllegalArgumentException("API key cannot be empty")
        }
        
        this.language = language ?: Locale.getDefault().language
        android.util.Log.d("AIReviewCompose-Init", "🌍 Language set to: ${this.language}")
        
        customInsightProvider?.let {
            this.insightProvider = it
            android.util.Log.d("AIReviewCompose-Init", "🔧 Custom insight provider set")
        }
        
        this.apiKey = apiKey
        this.currentModel = model
        this.currentProviderType = providerType
        
        android.util.Log.d("AIReviewCompose-Init", "🏭 Creating provider: ${providerType.displayName}")
        
        try {
            currentProvider = providerFactory.createProvider(providerType)
            
            android.util.Log.d("AIReviewCompose-Init", "🔄 Initializing provider...")
            currentProvider?.initialize(apiKey, model)
            
            currentProvider?.configure(this.language, this.insightProvider)
            
            android.util.Log.d("AIReviewCompose-Init", "✅ Provider initialization successful")
        } catch (e: Exception) {
            android.util.Log.e("AIReviewCompose-Init", "❌ Provider initialization failed: ${e.message}", e)
            throw e
        }
        
        isInitialized = true
        android.util.Log.d("AIReviewCompose-Init", "🎉 AIReviewCompose initialization completed!")
        android.util.Log.d("AIReviewCompose-Init", "📋 Provider: ${providerType.displayName}, Model: ${model?.displayName}")
    }
    
    /**
     * Legacy initialization method for backward compatibility
     */
    suspend fun initialize(
        apiKey: String, 
        model: AIModel? = null,
        language: String? = null,
        customInsightProvider: DomainInsightProvider? = null
    ) {
        val providerType = model?.provider ?: AIProviderType.OPENAI
        initialize(providerType, apiKey, model, language, customInsightProvider)
    }
    
    /**
     * Switches to a different provider
     * @param providerType New provider type
     * @param apiKey API key for the new provider
     * @param model Model to use with new provider (optional)
     */
    suspend fun switchProvider(
        providerType: AIProviderType,
        apiKey: String,
        model: AIModel? = null
    ) {
        android.util.Log.d("AIReviewCompose-Switch", "🔄 Switching to provider: ${providerType.displayName}")
        
        try {
            val newProvider = providerFactory.createProvider(providerType)
            
            newProvider.initialize(apiKey, model)
            newProvider.configure(this.language, this.insightProvider)
            
            currentProvider = newProvider
            currentProviderType = providerType
            this.apiKey = apiKey
            this.currentModel = model
            
            android.util.Log.d("AIReviewCompose-Switch", "✅ Provider switched successfully")
        } catch (e: Exception) {
            android.util.Log.e("AIReviewCompose-Switch", "❌ Provider switch failed: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Switches the current model (within same provider)
     * @param model New model to use
     */
    suspend fun switchModel(model: AIModel) {
        if (!isInitialized) {
            throw IllegalStateException("AIReviewCompose is not initialized.")
        }
        
        currentProvider?.let { provider ->
            android.util.Log.d("AIReviewCompose-Model", "🔄 Switching model to: ${model.displayName}")
            
            if (model.provider != currentProviderType) {
                throw IllegalArgumentException("Model ${model.displayName} is not compatible with current provider ${currentProviderType?.displayName}")
            }
            
            provider.switchModel(model)
            this.currentModel = model
            
            android.util.Log.d("AIReviewCompose-Model", "✅ Model switched successfully")
        } ?: throw IllegalStateException("No active provider")
    }
    
    /**
     * Checks if the library is initialized
     */
    fun isInitialized(): Boolean = isInitialized
    
    /**
     * Returns the current active provider type
     */
    fun getCurrentProviderType(): AIProviderType? = currentProviderType
    
    /**
     * Returns the current active model
     */
    fun getCurrentModel(): AIModel? = currentModel
    
    /**
     * Returns the analyzer (current provider - no fallback allowed)
     */
    fun getAnalyzer(): ComprehensiveAnalyzer {
        if (!isInitialized) {
            throw IllegalStateException("AIReviewCompose not initialized. Call initialize() first.")
        }
        
        return currentProvider as? ComprehensiveAnalyzer 
            ?: throw IllegalStateException("Current provider is not a comprehensive analyzer: ${currentProvider?.javaClass?.simpleName}")
    }
    
    /**
     * Returns available providers from factory
     */
    fun getAvailableProviders(): List<AIProviderType> {
        return providerFactory.getAvailableProviders()
    }
    
    /**
     * Returns detailed provider information
     */
    fun getProviderInfo(): List<ProviderFactory.ProviderInfo> {
        return providerFactory.getProviderInfo()
    }
    
    /**
     * Returns the current language setting
     */
    fun getLanguage(): String = language
    
    /**
     * Sets the language for AI responses
     * @param language Language code (e.g., "en", "tr", "es")
     */
    fun setLanguage(language: String) {
        this.language = language
        
        if (isInitialized) {
            currentProvider?.configure(this.language, this.insightProvider)
        }
        
        android.util.Log.d("AIReviewCompose-Config", "🌍 Language changed to: $language")
    }
    
    /**
     * Returns the current insight provider
     */
    fun getInsightProvider(): DomainInsightProvider = insightProvider
    
    /**
     * Sets a custom insight provider
     * @param provider Custom insight provider
     */
    fun setInsightProvider(provider: DomainInsightProvider) {
        this.insightProvider = provider
        
        if (isInitialized) {
            currentProvider?.configure(this.language, this.insightProvider)
        }
        
        android.util.Log.d("AIReviewCompose-Config", "🔧 Insight provider updated")
    }
} 