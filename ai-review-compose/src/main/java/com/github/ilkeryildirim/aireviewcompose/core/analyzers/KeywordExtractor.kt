package com.github.ilkeryildirim.aireviewcompose.core.analyzers

import com.github.ilkeryildirim.aireviewcompose.data.models.Keyword

interface KeywordExtractor {
    suspend fun extractKeywords(
        text: String,
        maxKeywords: Int
    ): List<Keyword>
} 