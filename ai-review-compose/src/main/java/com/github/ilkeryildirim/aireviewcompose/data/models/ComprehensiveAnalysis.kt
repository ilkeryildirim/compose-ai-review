package com.github.ilkeryildirim.aireviewcompose.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Review domains for different business categories
 */
enum class ReviewDomain(val displayName: String, val description: String, val emoji: String) {
    E_COMMERCE("E-Commerce", "Product reviews", "🛒"),
    FOOD("Food", "Food and beverage reviews", "🍕"),
    RESTAURANT("Restaurant", "Restaurant and venue reviews", "🏪"),
    SERVICE("Service", "Service and vendor reviews", "👥"),
    APP("App", "Mobile/Web application reviews", "📱"),
    OTHER("Other", "General reviews", "📝")
}



@Immutable
@Serializable
data class ComprehensiveAnalysis(
    val sentiment: SentimentResult,
    val keywords: List<Keyword>,
    val summary: Summary? = null,
    val totalReviews: Int,
    val overallRating: Float? = null,
    val detectedDomain: ReviewDomain? = null,
    val domainConfidence: Float = 0f,
    val domainSpecificInsights: Map<String, String> = emptyMap()
)

/**
 * Kategori içgörüsü
 */
@Immutable
@Serializable
data class CategoryInsight(
    val category: String,
    val sentiment: Sentiment,
    val keywords: List<String>,
    val mentionCount: Int,
    val percentage: Float
) 