package com.github.ilkeryildirim.aireviewcompose.sample.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.ilkeryildirim.aireviewcompose.data.models.Review
import com.github.ilkeryildirim.aireviewcompose.data.models.Sentiment

// Sentiment Helper Functions
fun getSentimentEmoji(sentiment: Sentiment): String = when (sentiment) {
    Sentiment.POSITIVE -> "😊"
    Sentiment.NEGATIVE -> "😞"
    Sentiment.NEUTRAL -> "😐"
    Sentiment.MIXED -> "🤔"
}

fun getSentimentText(sentiment: Sentiment): String = when (sentiment) {
    Sentiment.POSITIVE -> "Positive"
    Sentiment.NEGATIVE -> "Negative"
    Sentiment.NEUTRAL -> "Neutral"
    Sentiment.MIXED -> "Mixed"
}

@Composable
fun getSentimentColor(sentiment: Sentiment): Color = when (sentiment) {
    Sentiment.POSITIVE -> Color(0xFF4CAF50) // Green
    Sentiment.NEGATIVE -> Color(0xFFF44336) // Red
    Sentiment.NEUTRAL -> Color(0xFF9E9E9E) // Gray
    Sentiment.MIXED -> Color(0xFFFF9800) // Orange
}

/**
 * ✨ Dynamic category color system - generates colors based on category name hash
 */
fun getCategoryColor(category: String?): Color {
    if (category == null) return Color(0xFF9E9E9E) // Default gray
    
    // Generate consistent color based on category name hash
    val hash = category.hashCode()
    val hue = (hash % 360).toFloat()
    
    return when {
        // Quality-related categories - Green tones
        category.contains("quality", ignoreCase = true) || 
        category.contains("kalite", ignoreCase = true) ||
        category.contains("Quality", ignoreCase = true) -> Color(0xFF4CAF50)
        
        // Price-related categories - Blue tones  
        category.contains("price", ignoreCase = true) || 
        category.contains("fiyat", ignoreCase = true) ||
        category.contains("cost", ignoreCase = true) ||
        category.contains("Price", ignoreCase = true) -> Color(0xFF2196F3)
        
        // Delivery-related categories - Orange tones
        category.contains("delivery", ignoreCase = true) || 
        category.contains("teslimat", ignoreCase = true) ||
        category.contains("shipping", ignoreCase = true) ||
        category.contains("Delivery", ignoreCase = true) -> Color(0xFFFF9800)
        
        // Performance-related categories - Purple tones
        category.contains("performance", ignoreCase = true) || 
        category.contains("performans", ignoreCase = true) ||
        category.contains("speed", ignoreCase = true) ||
        category.contains("hız", ignoreCase = true) ||
        category.contains("Performance", ignoreCase = true) -> Color(0xFF9C27B0)
        
        // Service-related categories - Red tones
        category.contains("service", ignoreCase = true) || 
        category.contains("hizmet", ignoreCase = true) ||
        category.contains("support", ignoreCase = true) ||
        category.contains("destek", ignoreCase = true) ||
        category.contains("Service", ignoreCase = true) -> Color(0xFFF44336)
        
        // Feature/Taste-related categories - Cyan tones
        category.contains("feature", ignoreCase = true) || 
        category.contains("özellik", ignoreCase = true) ||
        category.contains("taste", ignoreCase = true) ||
        category.contains("tat", ignoreCase = true) ||
        category.contains("lezzet", ignoreCase = true) ||
        category.contains("flavor", ignoreCase = true) ||
        category.contains("Feature", ignoreCase = true) -> Color(0xFF00BCD4)
        
        // Packaging-related categories - Brown tones
        category.contains("packaging", ignoreCase = true) || 
        category.contains("paketleme", ignoreCase = true) ||
        category.contains("package", ignoreCase = true) ||
        category.contains("Packaging", ignoreCase = true) -> Color(0xFF795548)
        
        // Design-related categories - Pink tones
        category.contains("design", ignoreCase = true) || 
        category.contains("tasarım", ignoreCase = true) ||
        category.contains("ui", ignoreCase = true) ||
        category.contains("interface", ignoreCase = true) ||
        category.contains("Design", ignoreCase = true) -> Color(0xFFE91E63)
        
        // Temperature-related categories - specific colors
        category.contains("temperature", ignoreCase = true) ||
        category.contains("sıcaklık", ignoreCase = true) ||
        category.contains("soğuk", ignoreCase = true) ||
        category.contains("sıcak", ignoreCase = true) -> Color(0xFFFF5722)
        
        // Quantity/Portion-related categories - specific colors
        category.contains("portion", ignoreCase = true) ||
        category.contains("porsiyon", ignoreCase = true) ||
        category.contains("quantity", ignoreCase = true) ||
        category.contains("miktar", ignoreCase = true) -> Color(0xFF3F51B5)
        
        // Other/Default - Generate dynamic color based on hash
        else -> {
            // Generate color from HSV with controlled saturation and value
            val normalizedHue = ((hash % 360) + 360) % 360  // Ensure positive 0-360
            val saturation = 0.6f + (kotlin.math.abs(hash % 40)) / 100f  // 0.6-1.0
            val value = 0.8f + (kotlin.math.abs(hash % 20)) / 100f       // 0.8-1.0
            Color.hsv(normalizedHue.toFloat(), saturation, value)
        }
    }
}

fun getReviews(): List<Review> {
    return listOf(
        Review(
            id = "1",
            text = "Ürün kalitesi çok iyi, paketleme güzeldi. Hızlı kargo ile 2 günde geldi. Fiyatı da uygun. Tavsiye ederim.",
            rating = 5f,
            authorName = "Ahmet K.",
            date = System.currentTimeMillis() - 86400000 // 1 day ago
        ),
        Review(
            id = "2", 
            text = "Malzeme kalitesi beklediğimden iyi. Teslimat süresi biraz uzun oldu ama kurye güler yüzlüydü. Genel olarak memnunum.",
            rating = 4f,
            authorName = "Ayşe M.",
            date = System.currentTimeMillis() - 172800000 // 2 days ago
        ),
        Review(
            id = "3",
            text = "Ürün hasarlı geldi, müşteri hizmetleri ile iletişim kurmak zor oldu. İade süreci uzun sürdü. Memnun kalmadım.",
            rating = 2f,
            authorName = "Mehmet S.",
            date = System.currentTimeMillis() - 259200000 // 3 days ago
        ),
    )
} 