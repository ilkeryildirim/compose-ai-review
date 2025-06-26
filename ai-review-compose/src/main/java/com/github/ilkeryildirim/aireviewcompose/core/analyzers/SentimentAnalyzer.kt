package com.github.ilkeryildirim.aireviewcompose.core.analyzers

import com.github.ilkeryildirim.aireviewcompose.data.models.SentimentResult

interface SentimentAnalyzer {
    suspend fun analyzeSentiment(
        text: String,
        language: String = "tr"
    ): SentimentResult
} 