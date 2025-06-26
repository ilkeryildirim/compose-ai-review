package com.github.ilkeryildirim.aireviewcompose.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Keyword extracted from review text with dynamic categorization
 */
@Immutable
@Serializable
data class Keyword(
    val word: String,
    val relevance: Float,
    val count: Int = 1,
    val category: String? = null
)

 