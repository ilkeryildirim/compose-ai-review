package com.github.ilkeryildirim.aireviewcompose.core.analyzers

import com.github.ilkeryildirim.aireviewcompose.data.models.ComprehensiveAnalysis
import com.github.ilkeryildirim.aireviewcompose.data.models.Review
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain

/**
 * Comprehensive analyzer interface combining all analysis capabilities
 */
interface ComprehensiveAnalyzer : SentimentAnalyzer, KeywordExtractor, SummaryGenerator {
    suspend fun analyzeComprehensive(
        reviews: List<Review>,
        forcedDomain: ReviewDomain?,
        language: String
    ): ComprehensiveAnalysis
} 