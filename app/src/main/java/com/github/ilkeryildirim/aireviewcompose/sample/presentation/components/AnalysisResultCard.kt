package com.github.ilkeryildirim.aireviewcompose.sample.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.ilkeryildirim.aireviewcompose.data.models.*
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils.getSentimentColor
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils.getSentimentEmoji
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils.getSentimentText
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils.getCategoryColor

@Composable
fun AnalysisResultCard(
    analysis: ComprehensiveAnalysis
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card with Domain Info
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Analysis Results",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${analysis.totalReviews} reviews analyzed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Domain Information
                analysis.detectedDomain?.let { domain ->
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = domain.emoji,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Detected Category: ${domain.displayName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Confidence: ${(analysis.domainConfidence * 100).toInt()}% • ${domain.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Sentiment Analysis Card
        Card {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getSentimentEmoji(analysis.sentiment.sentiment),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${analysis.detectedDomain?.displayName ?: "General"} Sentiment Analysis",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = getSentimentText(analysis.sentiment.sentiment),
                            style = MaterialTheme.typography.bodyLarge,
                            color = getSentimentColor(analysis.sentiment.sentiment)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Confidence Score
                SentimentConfidenceIndicator(
                    confidence = analysis.sentiment.confidence,
                    sentiment = analysis.sentiment.sentiment
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sentiment Scores Chart
                SentimentScoresChart(scores = analysis.sentiment.scores)
            }
        }
        
        // Keywords Card
        Card {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔑",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Keywords",
                            style = MaterialTheme.typography.titleMedium
                        )
                        analysis.detectedDomain?.let { domain ->
                            Text(
                                text = "Specific to ${domain.displayName} category",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                KeywordsChipGrid(keywords = analysis.keywords.take(8))
            }
        }
        
        // Domain-Specific Insights
        if (analysis.domainSpecificInsights.isNotEmpty()) {
            DomainInsightsCard(
                domain = analysis.detectedDomain,
                insights = analysis.domainSpecificInsights
            )
        }
        
        // Summary Card
        analysis.summary?.let { summary ->
            Card {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📝",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Summary Report",
                                style = MaterialTheme.typography.titleMedium
                            )
                            analysis.detectedDomain?.let { domain ->
                                Text(
                                    text = "${domain.displayName} focused analysis",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = summary.text,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    
                    if (summary.highlights.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "✨ Highlights",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        summary.highlights.forEach { highlight ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = highlight,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Overall Rating
        analysis.overallRating?.let { rating ->
            OverallRatingCard(rating = rating, totalReviews = analysis.totalReviews)
        }
    }
}

@Composable
fun SentimentConfidenceIndicator(
    confidence: Float,
    sentiment: Sentiment
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Confidence Score",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "${(confidence * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = getSentimentColor(sentiment)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = confidence,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = getSentimentColor(sentiment),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun SentimentScoresChart(scores: SentimentScores) {
    Column {
        Text(
            text = "Detailed Sentiment Distribution",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Positive
        ScoreBar(
            label = "😊 Positive",
            score = scores.positive,
            color = Color(0xFF4CAF50)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Negative
        ScoreBar(
            label = "😞 Negative", 
            score = scores.negative,
            color = Color(0xFFF44336)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Neutral
        ScoreBar(
            label = "😐 Neutral",
            score = scores.neutral,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun ScoreBar(
    label: String,
    score: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = score,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun KeywordsChipGrid(keywords: List<Keyword>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 200.dp)
    ) {
        items(keywords) { keyword ->
            KeywordChip(keyword = keyword)
        }
    }
}

@Composable
fun KeywordChip(keyword: Keyword) {
    val categoryColor = getCategoryColor(keyword.category)
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = categoryColor.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = keyword.word,
                style = MaterialTheme.typography.labelLarge,
                color = categoryColor
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(keyword.relevance * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = keyword.category ?: "General",
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor
                )
            }
        }
    }
}

@Composable
fun OverallRatingCard(rating: Float, totalReviews: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Overall Rating",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = String.format("%.1f", rating),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = " / 5.0",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Star Rating Visual
            StarRatingVisual(rating = rating)
        }
    }
}

@Composable
fun StarRatingVisual(rating: Float) {
    Row {
        repeat(5) { index ->
            val starRating = (rating - index).coerceIn(0f, 1f)
            Text(
                text = when {
                    starRating >= 1f -> "⭐"
                    starRating >= 0.5f -> "🌟"
                    else -> "☆"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DomainInsightsCard(
    domain: ReviewDomain?,
    insights: Map<String, String>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${domain?.displayName ?: "General"} Insights",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Category-based analysis and recommendations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            insights.forEach { (key, value) ->
                InsightRow(
                    title = key,
                    content = value,
                    icon = getInsightIcon(key)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun InsightRow(
    title: String,
    content: String,
    icon: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

fun getInsightIcon(key: String): String = when {
    key.contains("Beğenilen") || key.contains("Değerlendirilen") || key.contains("Best") || key.contains("Most") -> "👍"
    key.contains("İyileştirme") || key.contains("Alanı") || key.contains("Improvement") || key.contains("Area") -> "⚠️"
    key.contains("Öner") || key.contains("Strateji") || key.contains("Recommend") || key.contains("Strategy") -> "💡"
    key.contains("Durum") || key.contains("Odak") || key.contains("Status") || key.contains("Focus") -> "🎯"
    else -> "📊"
} 