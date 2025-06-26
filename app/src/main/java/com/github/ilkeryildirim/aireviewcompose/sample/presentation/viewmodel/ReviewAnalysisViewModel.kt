package com.github.ilkeryildirim.aireviewcompose.sample.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ilkeryildirim.aireviewcompose.data.models.ComprehensiveAnalysis
import com.github.ilkeryildirim.aireviewcompose.data.models.Review
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain
import com.github.ilkeryildirim.aireviewcompose.domain.usecase.AnalyzeReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewAnalysisViewModel @Inject constructor(
    private val analyzeReviewsUseCase: AnalyzeReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewAnalysisUiState())
    val uiState: StateFlow<ReviewAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeReviews(
        reviews: List<Review>,
        forcedDomain: ReviewDomain? = null,
        language: String = "tr"
    ) {
        Log.d("ReviewAnalysisViewModel", "🚀 ViewModel.analyzeReviews called with ${reviews.size} reviews")
        Log.d("ReviewAnalysisViewModel", "🎯 Domain: ${forcedDomain?.displayName ?: "Auto-detect"}")
        Log.d("ReviewAnalysisViewModel", "🌍 Language: $language")
        
        viewModelScope.launch {
            Log.d("ReviewAnalysisViewModel", "⏳ Setting loading state...")
            _uiState.value = _uiState.value.copy(isLoading = true, analysisStep = 0, error = null)
            
            // Simulate analysis steps with delays
            try {
                // Step 0: Domain Detection
                _uiState.value = _uiState.value.copy(analysisStep = 0)
                delay(1000)
                
                // Step 1: Sentiment Analysis
                _uiState.value = _uiState.value.copy(analysisStep = 1)
                delay(1500)
                
                // Step 2: Keyword Extraction
                _uiState.value = _uiState.value.copy(analysisStep = 2)
                delay(1000)
                
                // Step 3: Summary Generation
                _uiState.value = _uiState.value.copy(analysisStep = 3)
                delay(1500)
                
                Log.d("ReviewAnalysisViewModel", "📞 Calling AnalyzeReviewsUseCase...")
                analyzeReviewsUseCase(reviews, forcedDomain, language)
                    .onSuccess { analysis ->
                        Log.d("ReviewAnalysisViewModel", "✅ Analysis successful! Updating UI state...")
                        Log.d("ReviewAnalysisViewModel", "📊 Analysis summary: ${analysis.sentiment.sentiment}, ${analysis.keywords.size} keywords")
                        
                        // Step 4: Complete
                        _uiState.value = _uiState.value.copy(analysisStep = 4)
                        delay(500)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            analysis = analysis,
                            error = null
                        )
                    }
                    .onFailure { exception ->
                        Log.e("ReviewAnalysisViewModel", "❌ Analysis failed: ${exception.message}", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
            } catch (e: Exception) {
                Log.e("ReviewAnalysisViewModel", "❌ Analysis failed: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        Log.d("ReviewAnalysisViewModel", "🧹 Clearing error state")
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun reset() {
        Log.d("ReviewAnalysisViewModel", "🔄 Resetting ViewModel state")
        _uiState.value = ReviewAnalysisUiState()
    }
}

@Immutable
data class ReviewAnalysisUiState(
    val isLoading: Boolean = false,
    val analysisStep: Int = 0,
    val analysis: ComprehensiveAnalysis? = null,
    val error: String? = null
) 