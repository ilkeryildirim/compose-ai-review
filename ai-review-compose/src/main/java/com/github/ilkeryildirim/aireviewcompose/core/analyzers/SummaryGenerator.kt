package com.github.ilkeryildirim.aireviewcompose.core.analyzers

import com.github.ilkeryildirim.aireviewcompose.data.models.Summary

interface SummaryGenerator {
    suspend fun generateSummary(
        texts: List<String>
    ): Summary
} 