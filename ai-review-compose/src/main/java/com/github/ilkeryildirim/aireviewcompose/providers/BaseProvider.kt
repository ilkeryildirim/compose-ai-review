package com.github.ilkeryildirim.aireviewcompose.providers

import com.github.ilkeryildirim.aireviewcompose.core.AIFeature
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProvider
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType

/**
 * Tüm AI provider'lar için temel sınıf
 */
abstract class BaseProvider : AIProvider {
    
    /**
     * Provider'ın kullanılabilir olup olmadığını kontrol eder
     */
    abstract override suspend fun isAvailable(): Boolean
    
    /**
     * API anahtarının geçerli olup olmadığını kontrol eder
     */
    abstract override suspend fun validateApiKey(apiKey: String): Boolean
    
    /**
     * Provider'ı başlatır
     */
    abstract override suspend fun initialize(apiKey: String, model: AIModel?)
    
    /**
     * Model değiştirir
     */
    abstract override suspend fun switchModel(model: AIModel)
    
    /**
     * Provider bilgilerini string olarak döndürür
     */
    override fun toString(): String {
        return "${providerType.displayName} - ${currentModel.displayName}"
    }
} 