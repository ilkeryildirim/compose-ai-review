package com.github.ilkeryildirim.aireviewcompose.sample.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType
import com.github.ilkeryildirim.aireviewcompose.sample.R
import com.github.ilkeryildirim.aireviewcompose.sample.utils.LocaleManager
import com.github.ilkeryildirim.aireviewcompose.sample.utils.LocalAppLanguage

/**
 * Inline configuration card with Material 3 design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineConfigurationCard(
    onConfigurationComplete: (AIProviderType, String, AIModel, String) -> Unit,
    onLanguageChange: (String) -> Unit = {},
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAppLanguage = LocalAppLanguage.current
    var selectedProvider by remember { mutableStateOf(AIProviderType.OPENAI) }
    var selectedModel by remember { mutableStateOf(AIModel.GPT_4O_MINI) }
    var apiKey by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(currentAppLanguage) }
    var showPassword by remember { mutableStateOf(false) }
    var apiKeyError by remember { mutableStateOf<String?>(null) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // API key validation
    fun validateApiKey(context: android.content.Context, provider: AIProviderType, key: String): String? {
        return when {
            key.isBlank() -> context.getString(R.string.api_key_empty)
            provider == AIProviderType.OPENAI && !key.startsWith("sk-") -> context.getString(R.string.api_key_openai_format)
            provider == AIProviderType.GOOGLE && !key.startsWith("AIza") -> context.getString(R.string.api_key_google_format)
            provider == AIProviderType.HUGGINGFACE && !key.startsWith("hf_") -> context.getString(R.string.api_key_huggingface_format)
            key.length < 10 -> context.getString(R.string.api_key_too_short)
            else -> null
        }
    }
    
    // Update model and clear API key when provider changes
    LaunchedEffect(selectedProvider) {
        selectedModel = selectedProvider.supportedModels.first()
        apiKey = "" // Clear API key when provider changes
        apiKeyError = null
    }
    
    // Validate API key when it changes
    LaunchedEffect(apiKey, selectedProvider) {
        apiKeyError = if (apiKey.isNotBlank()) validateApiKey(context, selectedProvider, apiKey) else null
    }
    
    // Update selected language when app language changes
    LaunchedEffect(currentAppLanguage) {
        selectedLanguage = currentAppLanguage
    }
    
    val canInitialize = apiKey.isNotBlank() && apiKeyError == null && !isLoading
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = stringResource(R.string.ai_setup),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.provider_configuration),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.provider_setup_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Provider Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.select_provider),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Provider chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val providers = listOf(
                        Triple(AIProviderType.OPENAI, "OpenAI", true),
                        Triple(AIProviderType.GOOGLE, "Google Gemini", true),
                        Triple(AIProviderType.HUGGINGFACE, "HuggingFace", false)
                    )
                    
                    items(providers) { (provider, name, isAvailable) ->
                        FilterChip(
                            onClick = { 
                                if (isAvailable) {
                                    selectedProvider = provider
                                }
                            },
                            label = { 
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (provider) {
                                            AIProviderType.OPENAI -> "🤖"
                                            AIProviderType.GOOGLE -> "⭐"
                                            AIProviderType.HUGGINGFACE -> "🤗"
                                        }
                                    )
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (!isAvailable) {
                                        Text(
                                            text = "(Yakında)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            selected = provider == selectedProvider,
                            enabled = isAvailable,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
            
            // Model Selection - Enhanced UI
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.select_model),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Model cards with enhanced design
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedProvider.supportedModels) { model ->
                        ModelCard(
                            model = model,
                            isSelected = selectedModel.modelId == model.modelId,
                            onClick = { selectedModel = model }
                        )
                    }
                }
            }
            
            // API Key Input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.api_key),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text(stringResource(R.string.api_key_label, selectedProvider.displayName)) },
                    placeholder = { 
                        Text(when (selectedProvider) {
                            AIProviderType.OPENAI -> "sk-proj-..."
                            AIProviderType.GOOGLE -> "AIza..."
                            AIProviderType.HUGGINGFACE -> "hf_..."
                        })
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = stringResource(R.string.api_key)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) stringResource(R.string.hide) else stringResource(R.string.show)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (canInitialize) {
                                onConfigurationComplete(selectedProvider, apiKey, selectedModel, selectedLanguage)
                            }
                        }
                    ),
                    isError = apiKeyError != null,
                    supportingText = apiKeyError?.let { 
                        { Text(it, color = MaterialTheme.colorScheme.error) } 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            // Language Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🌍 " + stringResource(R.string.language_selection),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                LanguageSelection(
                    currentLanguage = selectedLanguage,
                    onLanguageSelected = { newLanguage ->
                        selectedLanguage = newLanguage
                        onLanguageChange(newLanguage)
                    }
                )
            }
            
            // Error Display
            AnimatedVisibility(
                visible = error != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = stringResource(R.string.error),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Initialize Button
            Button(
                onClick = {
                    onConfigurationComplete(selectedProvider, apiKey, selectedModel, selectedLanguage)
                },
                enabled = canInitialize,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.initialize),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.initialize_provider, selectedProvider.displayName),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: AIModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(120.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp, 
                MaterialTheme.colorScheme.primary
            )
        } else null,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isSelected) {
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.0f)
                                )
                            }
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Model name and provider
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = model.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                    
                    Text(
                        text = model.provider.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Model specs
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "⚡ ${model.tokensPerMinute / 1000}K TPM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "💰 ${model.costEfficiency.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = when (model.costEfficiency.displayName) {
                            "High" -> MaterialTheme.colorScheme.primary
                            "Medium" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
            
            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.selected),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
} 