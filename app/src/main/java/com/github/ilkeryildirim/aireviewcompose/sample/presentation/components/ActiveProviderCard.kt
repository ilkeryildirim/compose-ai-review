package com.github.ilkeryildirim.aireviewcompose.sample.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType

@Composable
fun ActiveProviderCard(
    provider: AIProviderType,
    model: AIModel?,
    onChangeProvider: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Provider Info
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Column {
                    Text(
                        text = "Active Provider",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "${getProviderDisplayName(provider)} • ${model?.displayName ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    // Model specs in a simple format
                    model?.let {
                        Text(
                            text = "${it.tokensPerMinute / 1000}K TPM • ${it.costEfficiency.displayName} Cost",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            TextButton(
                onClick = onChangeProvider,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Change",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Change",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun getProviderDisplayName(provider: AIProviderType): String {
    return when (provider) {
        AIProviderType.OPENAI -> "OpenAI"
        AIProviderType.GOOGLE -> "Google AI"
        AIProviderType.HUGGINGFACE -> "HuggingFace"
    }
} 