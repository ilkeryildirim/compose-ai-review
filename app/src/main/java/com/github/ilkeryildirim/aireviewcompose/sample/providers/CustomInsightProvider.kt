package com.github.ilkeryildirim.aireviewcompose.sample.providers

import com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain

class CustomInsightProvider : DomainInsightProvider {
    
    override fun getInsights(domain: ReviewDomain, language: String): Map<String, String> {
        return when (language.lowercase()) {
            "tr" -> getTurkishCustomInsights(domain)
            "en" -> getEnglishCustomInsights(domain)
            else -> getEnglishCustomInsights(domain)
        }
    }
    
    override fun getDomainContext(domain: ReviewDomain, language: String): String {
        return when (language.lowercase()) {
            "tr" -> getTurkishCustomContext(domain)
            "en" -> getEnglishCustomContext(domain)
            else -> getEnglishCustomContext(domain)
        }
    }
    
    private fun getTurkishCustomInsights(domain: ReviewDomain): Map<String, String> {
        return when (domain) {
            ReviewDomain.FOOD -> mapOf(
                "🍯 En Çok Beğenilen" to "Özel soslar ve tazelik",
                "🚫 İyileştirme Alanları" to "Porsiyon tutarlılığı ve sunum",
                "💡 Öneriler" to "Mevsimlik menü ve vegan seçenekler"
            )
            ReviewDomain.E_COMMERCE -> mapOf(
                "⭐ Güçlü Yönler" to "Hızlı teslimat ve kaliteli ambalaj",
                "⚠️ Dikkat Edilmesi Gerekenler" to "Ürün açıklamaları ve fotoğraf kalitesi",
                "🎯 Strateji Önerileri" to "Müşteri geri bildirim sistemi geliştir"
            )
            else -> mapOf(
                "📈 Genel Durum" to "Ortalama memnuniyet seviyesi",
                "🔍 Odak Alanları" to "Müşteri deneyimi iyileştirme",
                "⚡ Öncelikler" to "Hızlı çözüm ve iletişim"
            )
        }
    }
    
    private fun getEnglishCustomInsights(domain: ReviewDomain): Map<String, String> {
        return when (domain) {
            ReviewDomain.FOOD -> mapOf(
                "🍯 Most Loved" to "Special sauces and freshness",
                "🚫 Areas for Improvement" to "Portion consistency and presentation",
                "💡 Recommendations" to "Seasonal menu and vegan options"
            )
            ReviewDomain.E_COMMERCE -> mapOf(
                "⭐ Strengths" to "Fast delivery and quality packaging",
                "⚠️ Watch Points" to "Product descriptions and photo quality",
                "🎯 Strategy Suggestions" to "Develop customer feedback system"
            )
            else -> mapOf(
                "📈 Overall Status" to "Average satisfaction level",
                "🔍 Focus Areas" to "Customer experience improvement",
                "⚡ Priorities" to "Quick resolution and communication"
            )
        }
    }
    
    private fun getTurkishCustomContext(domain: ReviewDomain): String {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> "ürün kalitesi, teslimat deneyimi ve müşteri memnuniyeti açısından"
            ReviewDomain.FOOD -> "lezzet profili, sunum kalitesi ve genel yemek deneyimi açısından"
            ReviewDomain.RESTAURANT -> "atmosfer, hizmet kalitesi ve genel deneyim açısından"
            ReviewDomain.SERVICE -> "müşteri desteği, çözüm hızı ve güvenilirlik açısından"
            ReviewDomain.APP -> "kullanıcı arayüzü, performans ve özellik zenginliği açısından"
            ReviewDomain.OTHER -> "genel memnuniyet ve kullanıcı deneyimi açısından"
        }
    }
    
    private fun getEnglishCustomContext(domain: ReviewDomain): String {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> "in terms of product quality, delivery experience and customer satisfaction"
            ReviewDomain.FOOD -> "in terms of flavor profile, presentation quality and overall dining experience"
            ReviewDomain.RESTAURANT -> "in terms of atmosphere, service quality and overall experience"
            ReviewDomain.SERVICE -> "in terms of customer support, solution speed and reliability"
            ReviewDomain.APP -> "in terms of user interface, performance and feature richness"
            ReviewDomain.OTHER -> "in terms of general satisfaction and user experience"
        }
    }
} 