package com.github.ilkeryildirim.aireviewcompose.core

import android.content.Context
import com.github.ilkeryildirim.aireviewcompose.providers.openai.OpenAIProvider
import com.github.ilkeryildirim.aireviewcompose.providers.gemini.GeminiProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple provider factory - no KSP needed!
 * Easily maintainable and debuggable
 */
@Singleton
class ProviderFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Creates AI provider by type
     * @param providerType Provider type to create
     * @return AIProvider instance
     */
    fun createProvider(providerType: AIProviderType): AIProvider {
        return when (providerType) {
            AIProviderType.OPENAI -> OpenAIProvider(context)
            AIProviderType.GOOGLE -> GeminiProvider()
            AIProviderType.HUGGINGFACE -> throw UnsupportedOperationException("HuggingFace provider not implemented yet")
        }
    }
    
    /**
     * Returns list of available/implemented providers
     */
    fun getAvailableProviders(): List<AIProviderType> {
        return listOf(
            AIProviderType.OPENAI,
            AIProviderType.GOOGLE

        )
    }
    
    /**
     * Provider metadata for UI display
     */
    data class ProviderInfo(
        val type: AIProviderType,
        val displayName: String,
        val description: String,
        val supportedModels: List<AIModel>,
        val features: Set<AIFeature>,
        val priority: Int = 1
    )
    
    /**
     * Returns detailed provider information
     */
    fun getProviderInfo(): List<ProviderInfo> {
        return listOf(
            ProviderInfo(
                type = AIProviderType.OPENAI,
                displayName = "OpenAI",
                description = "ChatGPT and GPT-4 models with excellent analysis quality",
                supportedModels = AIProviderType.OPENAI.supportedModels,
                features = setOf(
                    AIFeature.SENTIMENT_ANALYSIS,
                    AIFeature.KEYWORD_EXTRACTION,
                    AIFeature.SUMMARY_GENERATION,
                    AIFeature.COMPREHENSIVE_ANALYSIS,
                    AIFeature.DOMAIN_DETECTION
                ),
                priority = 1
            ),
            ProviderInfo(
                type = AIProviderType.GOOGLE,
                displayName = "Google Gemini",
                description = "Google's powerful Gemini models with fast processing",
                supportedModels = AIProviderType.GOOGLE.supportedModels,
                features = setOf(
                    AIFeature.SENTIMENT_ANALYSIS,
                    AIFeature.KEYWORD_EXTRACTION,
                    AIFeature.SUMMARY_GENERATION,
                    AIFeature.DOMAIN_DETECTION
                ),
                priority = 2
            )
        )
    }
    
    /**
     * Checks if provider is available and ready to use
     */
    suspend fun isProviderAvailable(providerType: AIProviderType): Boolean {
        return try {
            val provider = createProvider(providerType)
            provider.isAvailable()
        } catch (e: Exception) {
            false
        }
    }
} 