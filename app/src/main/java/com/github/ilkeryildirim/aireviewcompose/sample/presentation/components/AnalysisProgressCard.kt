package com.github.ilkeryildirim.aireviewcompose.sample.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AnalysisStep(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false
)

@Composable
fun AnalysisProgressCard(
    isAnalyzing: Boolean,
    currentStep: Int = 0,
    modifier: Modifier = Modifier
) {
    val steps = remember {
        listOf(
            AnalysisStep(
                id = "domain",
                title = "Domain Detection",
                subtitle = "Analyzing review category...",
                icon = Icons.Default.Category,
                color = Color(0xFF6366F1)
            ),
            AnalysisStep(
                id = "sentiment",
                title = "Sentiment Analysis",
                subtitle = "Understanding emotions...",
                icon = Icons.Default.Mood,
                color = Color(0xFF10B981)
            ),
            AnalysisStep(
                id = "keywords",
                title = "Keyword Extraction",
                subtitle = "Finding important terms...",
                icon = Icons.Default.Key,
                color = Color(0xFFF59E0B)
            ),
            AnalysisStep(
                id = "summary",
                title = "Summary Generation",
                subtitle = "Creating comprehensive summary...",
                icon = Icons.Default.Summarize,
                color = Color(0xFFEF4444)
            ),
            AnalysisStep(
                id = "complete",
                title = "Analysis Complete",
                subtitle = "Results ready!",
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF059669)
            )
        )
    }

    if (isAnalyzing) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Analysis",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Analysis in Progress",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Please wait while we analyze your reviews",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progress Steps
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    steps.forEachIndexed { index, step ->
                        AnalysisStepItem(
                            step = step.copy(
                                isCompleted = index < currentStep,
                                isActive = index == currentStep
                            ),
                            isLast = index == steps.size - 1
                        )
                    }
                }

                // Overall Progress
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(currentStep + 1).coerceAtMost(steps.size)}/${steps.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = { ((currentStep + 1).toFloat() / steps.size).coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisStepItem(
    step: AnalysisStep,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "step_animation")
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step Icon
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = when {
                            step.isCompleted -> step.color
                            step.isActive -> step.color.copy(alpha = pulseAlpha)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
            )
            
            // Icon
            Icon(
                imageVector = if (step.isCompleted) Icons.Default.Check else step.icon,
                contentDescription = step.title,
                tint = when {
                    step.isCompleted -> Color.White
                    step.isActive -> Color.White
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(18.dp)
            )
        }

        // Step Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = when {
                    step.isCompleted -> MaterialTheme.colorScheme.onSurface
                    step.isActive -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Text(
                text = step.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (step.isActive) 1f else 0.7f
                )
            )
        }

        // Loading indicator for active step
        if (step.isActive && !step.isCompleted) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = step.color
            )
        }
    }
} 