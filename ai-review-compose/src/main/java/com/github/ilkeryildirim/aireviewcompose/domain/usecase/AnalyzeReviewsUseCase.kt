package com.github.ilkeryildirim.aireviewcompose.domain.usecase

import android.util.Log
import com.github.ilkeryildirim.aireviewcompose.AIReviewCompose
import com.github.ilkeryildirim.aireviewcompose.data.models.ComprehensiveAnalysis
import com.github.ilkeryildirim.aireviewcompose.data.models.Review
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain
import javax.inject.Inject

/**
 * Use case for analyzing reviews comprehensively using dynamic provider selection
 */
class AnalyzeReviewsUseCase @Inject constructor(
    private val aiReviewCompose: AIReviewCompose
) {
    
    /**
     * Analyzes reviews comprehensively using the currently configured provider
     * @param reviews Reviews to analyze
     * @param forcedDomain Optional domain override - if provided, skips auto-detection
     * @param language Language for analysis prompts (default: "tr" for Turkish)
     * @return Analysis result
     */
    suspend operator fun invoke(
        reviews: List<Review>,
        forcedDomain: ReviewDomain? = null,
        language: String = "tr"
    ): Result<ComprehensiveAnalysis> {
        return try {
            Log.d("AIReviewCompose-UseCase", "Starting analysis for ${reviews.size} reviews")
            
            if (reviews.isEmpty()) {
                Log.w("AIReviewCompose-UseCase", "Empty reviews list provided")
                return Result.failure(IllegalArgumentException("Review list cannot be empty"))
            }
            
            if (!aiReviewCompose.isInitialized()) {
                Log.e("AIReviewCompose-UseCase", "AIReviewCompose is not initialized")
                return Result.failure(IllegalStateException("AI provider not initialized. Please configure a provider first."))
            }
            
            val currentProvider = aiReviewCompose.getCurrentProviderType()
            val currentModel = aiReviewCompose.getCurrentModel()
            
            Log.d("AIReviewCompose-UseCase", "Using provider: ${currentProvider?.displayName}")
            Log.d("AIReviewCompose-UseCase", "Using model: ${currentModel?.displayName}")
            Log.d("AIReviewCompose-UseCase", "Forced domain: ${forcedDomain?.displayName ?: "Auto-detect"}")
            Log.d("AIReviewCompose-UseCase", "Language: $language")
            Log.d("AIReviewCompose-UseCase", "Sample review text: ${reviews.first().text.take(50)}...")
            
            val analyzer = aiReviewCompose.getAnalyzer()
            Log.d("AIReviewCompose-UseCase", "Analyzer type: ${analyzer::class.simpleName}")
            
            val analysis = analyzer.analyzeComprehensive(reviews, forcedDomain, language)
            
            Log.d("AIReviewCompose-UseCase", "Analysis completed successfully")
            Log.d("AIReviewCompose-UseCase", "Result sentiment: ${analysis.sentiment.sentiment}")
            Log.d("AIReviewCompose-UseCase", "Result keywords count: ${analysis.keywords.size}")
            Log.d("AIReviewCompose-UseCase", "Detected domain: ${analysis.detectedDomain?.displayName}")
            
            Result.success(analysis)
        } catch (e: Exception) {
            Log.e("AIReviewCompose-UseCase", "Analysis failed: ${e.message}", e)
            Result.failure(e)
        }
    }
} 