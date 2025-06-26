package com.github.ilkeryildirim.aireviewcompose.sample

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.ilkeryildirim.aireviewcompose.AIReviewCompose
import com.github.ilkeryildirim.aireviewcompose.core.AIModel
import com.github.ilkeryildirim.aireviewcompose.core.AIProviderType
import com.github.ilkeryildirim.aireviewcompose.data.models.Review
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.components.*
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils.getReviews
import com.github.ilkeryildirim.aireviewcompose.sample.utils.LocaleManager
import com.github.ilkeryildirim.aireviewcompose.sample.utils.AppLanguageProvider
import com.github.ilkeryildirim.aireviewcompose.sample.utils.LocalAppLanguage
import com.github.ilkeryildirim.aireviewcompose.sample.presentation.viewmodel.ReviewAnalysisViewModel
import com.github.ilkeryildirim.aireviewcompose.sample.ui.theme.AIReviewComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var aiReviewCompose: AIReviewCompose

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let {
            LocaleManager.setLocale(it, LocaleManager.getLanguage(it))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("AIReviewCompose", "MainActivity started")

        setContent {
            AIReviewComposeTheme {
                var currentLanguage by remember {
                    mutableStateOf<String>(
                        LocaleManager.getCurrentLanguage(
                            this@MainActivity
                        )
                    )
                }

                AppLanguageProvider(
                    currentLanguage = currentLanguage,
                    onLanguageChange = { newLanguage ->
                        currentLanguage = newLanguage
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    ) {
                        ModernSampleScreen(
                            aiReviewCompose = aiReviewCompose,
                            currentLanguage = currentLanguage,
                            onLanguageChange = { newLanguage ->
                                LocaleManager.setLocale(this@MainActivity, newLanguage)
                                currentLanguage = newLanguage
                                recreate()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSampleScreen(
    aiReviewCompose: AIReviewCompose,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var isInitialized by rememberSaveable {
        mutableStateOf<Boolean>(
            aiReviewCompose.isInitialized() || LocaleManager.getInitState(context)
        )
    }
    var isChangingProvider by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }
    var reviews by remember { mutableStateOf(getReviews()) }

    var currentProvider by rememberSaveable { mutableStateOf(AIProviderType.OPENAI) }
    var currentModel by rememberSaveable { mutableStateOf(AIModel.GPT_4O_MINI) }

    LaunchedEffect(Unit) {
        isInitialized = aiReviewCompose.isInitialized()
        if (isInitialized) {
            aiReviewCompose.getCurrentProviderType()?.let { currentProvider = it }
            aiReviewCompose.getCurrentModel()?.let { currentModel = it }
        }
    }

            LaunchedEffect(currentLanguage) {
            if (isInitialized && !isChangingProvider) {
                aiReviewCompose.setLanguage(currentLanguage)
                LocaleManager.saveInitState(context, true)
                Log.d("AIReviewCompose", "Language changed to: $currentLanguage")
            }
            
            if (isChangingProvider) {
                isInitialized = false
                isChangingProvider = false
                LocaleManager.saveInitState(context, false)
                Log.d("AIReviewCompose", "Provider change mode - showing configuration after language change")
            }
        }

    val scope = rememberCoroutineScope()

    key(currentLanguage) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ModernHeader()
            }

            item {
                if (isInitialized) {
                    ActiveProviderCard(
                        provider = currentProvider,
                        model = currentModel,
                        onChangeProvider = {
                            isChangingProvider = true
                            isInitialized = false
                            LocaleManager.saveInitState(context, false)
                            initError = null
                            Log.d("AIReviewCompose", "Showing inline configuration - provider change mode")
                        }
                    )
                } else {
                    InlineConfigurationCard(
                        onConfigurationComplete = { provider, apiKey, model, language ->
                            scope.launch {
                                try {
                                    isLoading = true
                                    initError = null

                                    Log.d("AIReviewCompose", "Starting configuration with:")
                                    Log.d("AIReviewCompose", "Provider: ${provider.displayName}")
                                    Log.d("AIReviewCompose", "Model: ${model.displayName}")
                                    Log.d("AIReviewCompose", "Language: $language")

                                    aiReviewCompose.initialize(
                                        providerType = provider,
                                        apiKey = apiKey,
                                        model = model,
                                        language = language
                                    )

                                    currentProvider = provider
                                    currentModel = model
                                    isInitialized = true
                                    isChangingProvider = false
                                    LocaleManager.saveInitState(context, true)

                                    Log.d("AIReviewCompose", "Configuration successful")

                                    onLanguageChange(language)
                                } catch (e: Exception) {
                                    Log.e("AIReviewCompose", "Configuration failed", e)
                                    initError = e.message
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        onLanguageChange = onLanguageChange,
                        isLoading = isLoading,
                        error = initError
                    )
                }
            }

            if (isInitialized) {
                item {
                    ReviewManagementScreen(
                        reviews = reviews,
                        onReviewsUpdated = { updatedReviews ->
                            reviews = updatedReviews
                            Log.d(
                                "AIReviewCompose",
                                "Reviews updated: ${reviews.size} total reviews"
                            )
                        },
                        selectedLanguage = currentLanguage,
                        currentProvider = currentProvider,
                        currentModel = currentModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.demo_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = stringResource(R.string.demo_title),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ReviewManagementScreen(
    reviews: List<Review>,
    onReviewsUpdated: (List<Review>) -> Unit,
    selectedLanguage: String,
    currentProvider: AIProviderType,
    currentModel: AIModel
) {
    val viewModel: ReviewAnalysisViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var selectedDomain by remember { mutableStateOf<ReviewDomain?>(null) }
    var isAutoDetect by remember { mutableStateOf(true) }

    LaunchedEffect(currentProvider, currentModel) {
        Log.d("AIReviewCompose", "Provider or model changed, clearing analysis results")
        Log.d(
            "AIReviewCompose",
            "New provider: ${currentProvider.displayName}, New model: ${currentModel.displayName}"
        )
        viewModel.reset()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DomainSelectionCard(
            selectedDomain = selectedDomain,
            isAutoDetect = isAutoDetect,
            onDomainChange = { selectedDomain = it },
            onAutoDetectChange = { isAutoDetect = it }
        )

        AnalysisControlCard(
            reviewsCount = reviews.size,
            isLoading = uiState.isLoading,
            onAnalyze = {
                Log.d("AIReviewCompose", "Starting analysis for ${reviews.size} reviews")
                val forcedDomain = if (isAutoDetect) null else selectedDomain
                viewModel.analyzeReviews(reviews, forcedDomain, selectedLanguage)
            }
        )

        AnimatedVisibility(
            visible = uiState.isLoading || uiState.error != null || uiState.analysis != null,
            enter = slideInVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(animationSpec = tween(300)) + fadeOut(
                animationSpec = tween(
                    300
                )
            )
        ) {
            when {
                uiState.isLoading -> AnalysisProgressCard(
                    isAnalyzing = true,
                    currentStep = uiState.analysisStep
                )

                uiState.error != null -> ErrorCard(
                    error = uiState.error!!,
                    onRetry = {
                        Log.d("AIReviewCompose", "Retrying analysis")
                        viewModel.clearError()
                        val forcedDomain = if (isAutoDetect) null else selectedDomain
                        viewModel.analyzeReviews(reviews, forcedDomain, selectedLanguage)
                    }
                )

                uiState.analysis != null -> AnalysisResultCard(analysis = uiState.analysis!!)
            }
        }

        AddReviewCard(
            onReviewAdded = { newReview ->
                val updatedReviews = reviews + newReview
                onReviewsUpdated(updatedReviews)
            },
            reviewsCount = reviews.size
        )

        Text(
            text = stringResource(R.string.reviews_count, reviews.size),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        reviews.forEachIndexed { index, review ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(300, delayMillis = index * 50),
                    initialOffsetY = { it }
                ) + fadeIn(animationSpec = tween(300, delayMillis = index * 50))
            ) {
                ReviewCard(
                    review = review,
                    onDelete = {
                        val updatedReviews = reviews.toMutableList().apply { removeAt(index) }
                        onReviewsUpdated(updatedReviews)
                    }
                )
            }
        }
    }
}

@Composable
private fun AnalysisControlCard(
    reviewsCount: Int,
    isLoading: Boolean,
    onAnalyze: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.ai_analysis),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.reviews_ready, reviewsCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Button(
                onClick = onAnalyze,
                enabled = reviewsCount > 0 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.analyze_button))
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.analysis_error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
}

