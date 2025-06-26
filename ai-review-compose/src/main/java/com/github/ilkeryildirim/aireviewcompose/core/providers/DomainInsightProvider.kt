package com.github.ilkeryildirim.aireviewcompose.core.providers

import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain

/**
 * Interface for providing domain-specific insights
 * Allows customization of insights based on the domain and language
 */
interface DomainInsightProvider {

    fun getInsights(domain: ReviewDomain, language: String): Map<String, String>

    fun getDomainContext(domain: ReviewDomain, language: String): String
}

/**
 * Default implementation providing basic insights
 */
class DefaultDomainInsightProvider : DomainInsightProvider {
    
    override fun getInsights(domain: ReviewDomain, language: String): Map<String, String> {
        return when (language.lowercase()) {
            "tr" -> getTurkishInsights(domain)
            else -> getEnglishInsights(domain)
        }
    }
    
    override fun getDomainContext(domain: ReviewDomain, language: String): String {
        return when (language.lowercase()) {
            "tr" -> getTurkishDomainContext(domain)
            else -> getEnglishDomainContext(domain)
        }
    }
    
    private fun getEnglishInsights(domain: ReviewDomain): Map<String, String> {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> mapOf(
                "Most Valued" to "Product quality and delivery speed",
                "Common Issues" to "Packaging and shipping delays",
                "Satisfaction Drivers" to "Value for money and customer service"
            )
            ReviewDomain.FOOD -> mapOf(
                "Most Valued" to "Taste and freshness",
                "Common Issues" to "Temperature and portion size",
                "Satisfaction Drivers" to "Flavor quality and presentation"
            )
            ReviewDomain.RESTAURANT -> mapOf(
                "Most Valued" to "Service quality and atmosphere",
                "Common Issues" to "Waiting time and cleanliness",
                "Satisfaction Drivers" to "Staff friendliness and ambiance"
            )
            ReviewDomain.SERVICE -> mapOf(
                "Most Valued" to "Response time and reliability",
                "Common Issues" to "Communication and availability",
                "Satisfaction Drivers" to "Professional support and problem resolution"
            )
            ReviewDomain.APP -> mapOf(
                "Most Valued" to "User experience and performance",
                "Common Issues" to "Bugs and slow loading",
                "Satisfaction Drivers" to "Ease of use and feature completeness"
            )
            ReviewDomain.OTHER -> mapOf(
                "Most Valued" to "Overall satisfaction",
                "Common Issues" to "General concerns",
                "Satisfaction Drivers" to "Meeting expectations"
            )
        }
    }
    
    private fun getTurkishInsights(domain: ReviewDomain): Map<String, String> {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> mapOf(
                "En Değerlendirilen" to "Ürün kalitesi ve teslimat hızı",
                "Yaygın Sorunlar" to "Ambalajlama ve kargo gecikmeleri",
                "Memnuniyet Faktörleri" to "Fiyat-performans oranı ve müşteri hizmeti"
            )
            ReviewDomain.FOOD -> mapOf(
                "En Değerlendirilen" to "Lezzet ve tazelik",
                "Yaygın Sorunlar" to "Sıcaklık ve porsiyon büyüklüğü",
                "Memnuniyet Faktörleri" to "Tat kalitesi ve sunum"
            )
            ReviewDomain.RESTAURANT -> mapOf(
                "En Değerlendirilen" to "Hizmet kalitesi ve atmosfer",
                "Yaygın Sorunlar" to "Bekleme süresi ve temizlik",
                "Memnuniyet Faktörleri" to "Personel nezaketi ve ortam"
            )
            ReviewDomain.SERVICE -> mapOf(
                "En Değerlendirilen" to "Yanıt süresi ve güvenilirlik",
                "Yaygın Sorunlar" to "İletişim ve erişilebilirlik",
                "Memnuniyet Faktörleri" to "Profesyonel destek ve problem çözme"
            )
            ReviewDomain.APP -> mapOf(
                "En Değerlendirilen" to "Kullanıcı deneyimi ve performans",
                "Yaygın Sorunlar" to "Hatalar ve yavaş yükleme",
                "Memnuniyet Faktörleri" to "Kullanım kolaylığı ve özellik bütünlüğü"
            )
            ReviewDomain.OTHER -> mapOf(
                "En Değerlendirilen" to "Genel memnuniyet",
                "Yaygın Sorunlar" to "Genel endişeler",
                "Memnuniyet Faktörleri" to "Beklentileri karşılama"
            )
        }
    }
    
    private fun getEnglishDomainContext(domain: ReviewDomain): String {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> "in terms of product quality, delivery, pricing, packaging"
            ReviewDomain.FOOD -> "in terms of taste, freshness, portion size, ingredient quality"
            ReviewDomain.RESTAURANT -> "in terms of atmosphere, service quality, cleanliness, location"
            ReviewDomain.SERVICE -> "in terms of customer satisfaction, support quality, reliability"
            ReviewDomain.APP -> "in terms of user experience, performance, features"
            ReviewDomain.OTHER -> "in terms of general satisfaction"
        }
    }
    
    private fun getTurkishDomainContext(domain: ReviewDomain): String {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> "ürün kalitesi, teslimat, fiyat, paketleme açısından"
            ReviewDomain.FOOD -> "lezzet, tazelik, porsiyon büyüklüğü, malzeme kalitesi açısından"
            ReviewDomain.RESTAURANT -> "atmosfer, hizmet kalitesi, temizlik, lokasyon açısından"
            ReviewDomain.SERVICE -> "müşteri memnuniyeti, destek kalitesi, güvenilirlik açısından"
            ReviewDomain.APP -> "kullanıcı deneyimi, performans, özellikler açısından"
            ReviewDomain.OTHER -> "genel memnuniyet açısından"
        }
    }
} 