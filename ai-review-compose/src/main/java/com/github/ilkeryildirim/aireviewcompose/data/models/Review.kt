package com.github.ilkeryildirim.aireviewcompose.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Review(
    val id: String,
    val text: String,
    val rating: Float? = null,
    val authorName: String? = null,
    val authorId: String? = null,
    val date: Long = System.currentTimeMillis(),
    val language: String = "tr",
    val metadata: Map<String, String> = emptyMap()
) 