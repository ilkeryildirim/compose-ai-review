package com.github.ilkeryildirim.aireviewcompose.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Summary(
    val text: String,
    val originalCount: Int,
    val confidence: Float,
    val highlights: List<String> = emptyList(),
    val language: String = "tr",
    val generatedAt: Long = System.currentTimeMillis()
) 