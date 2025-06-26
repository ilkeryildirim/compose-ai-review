package com.github.ilkeryildirim.aireviewcompose.sample.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain

@Composable
fun DomainSelectionCard(
    selectedDomain: ReviewDomain?,
    isAutoDetect: Boolean,
    onDomainChange: (ReviewDomain?) -> Unit,
    onAutoDetectChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Domain Selection",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            // Auto-detect Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🤖 Auto-detect domain",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = isAutoDetect,
                    onCheckedChange = onAutoDetectChange
                )
            }
            
            // Manual Domain Selection
            if (!isAutoDetect) {
                Text(
                    text = "Select Domain:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(ReviewDomain.entries) { domain ->
                        DomainSelectionChip(
                            domain = domain,
                            isSelected = selectedDomain == domain,
                            onSelect = { onDomainChange(domain) }
                        )
                    }
                }
            }
            
            // Status Text
            if (isAutoDetect) {
                Text(
                    text = "✨ AI will automatically detect the review domain",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = selectedDomain?.let { 
                        "📌 Selected: ${it.emoji} ${it.displayName} - ${it.description}"
                    } ?: "⚠️ Please select a domain",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun DomainSelectionChip(
    domain: ReviewDomain,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    FilterChip(
        onClick = onSelect,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = domain.emoji,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = domain.displayName,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        selected = isSelected,
        modifier = Modifier.fillMaxWidth()
    )
} 