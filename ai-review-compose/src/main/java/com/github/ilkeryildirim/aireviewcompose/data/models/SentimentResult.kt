package com.github.ilkeryildirim.aireviewcompose.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Duygu analizi sonucu
 */
@Immutable
@Serializable
data class SentimentResult(
    val sentiment: Sentiment,
    val confidence: Float,
    val scores: SentimentScores,
    val isOffline: Boolean = false,
    val providerType: String? = null
)

/**
 * Duygu durumu
 */
@Serializable
enum class Sentiment {
    POSITIVE,
    NEGATIVE,
    NEUTRAL,
    MIXED
}

/**
 * Duygu skorları
 */
@Immutable
@Serializable
data class SentimentScores(
    val positive: Float,
    val negative: Float,
    val neutral: Float,
    val mixed: Float = 0f
) 