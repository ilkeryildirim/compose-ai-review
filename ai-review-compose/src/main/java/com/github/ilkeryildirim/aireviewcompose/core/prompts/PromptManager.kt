package com.github.ilkeryildirim.aireviewcompose.core.prompts

import com.github.ilkeryildirim.aireviewcompose.data.models.ReviewDomain

/**
 * Base prompt manager for different AI providers
 * Contains standardized prompts that can be used across OpenAI, Gemini, etc.
 */
object PromptManager {
    fun generateSentimentPrompt(text: String, language: String, domain: ReviewDomain? = null): String {
        val domainContext = domain?.let { getDomainContext(it) } ?: "general satisfaction"
        
        return """
            Analyze the following text for sentiment analysis in terms of $domainContext. 
            Return the result in JSON format:
            {
              "sentiment": "POSITIVE|NEGATIVE|NEUTRAL|MIXED",
              "confidence": 0.95,
              "scores": {
                "positive": 0.8,
                "negative": 0.1,
                "neutral": 0.1,
                "mixed": 0.0
              }
            }
            
            Text to analyze: "$text"
            Response language: $language
        """.trimIndent()
    }

    fun generateKeywordPrompt(text: String, maxKeywords: Int, domain: ReviewDomain? = null): String {
        val domainHint = domain?.let { "Focus on ${it.displayName.lowercase()}-related keywords. " } ?: ""
        
        return """
            Extract the $maxKeywords most important keywords from the following text. 
            $domainHint
            Return in JSON format:
            {
              "keywords": [
                {
                  "word": "quality",
                  "relevance": 0.95,
                  "count": 3,
                  "category": "Quality"
                }
              ]
            }
            
            Create appropriate categories dynamically based on the context.
            Text: "$text"
        """.trimIndent()
    }

    fun generateSummaryPrompt(texts: List<String>, language: String, domain: ReviewDomain? = null): String {
        val domainFocus = domain?.let { getDomainContext(it) } ?: "general satisfaction and experience"
        val combinedText = texts.joinToString(" | ")
        
        return """
            Summarize the following reviews. Focus on $domainFocus. 
            Return in JSON format:
            {
              "text": "General summary text",
              "confidence": 0.85,
              "highlights": ["Important point 1", "Important point 2"]
            }
            
            Reviews: "$combinedText"
            Response language: $language
        """.trimIndent()
    }

    fun generateDomainDetectionPrompt(text: String): String {
        return """
            Analyze the following reviews carefully and detect which domain they belong to. 
            Pay attention to KEYWORDS!
            
            Return result in JSON format:
            {
              "domain": "E_COMMERCE|FOOD|RESTAURANT|SERVICE|APP|OTHER",
              "confidence": 0.85,
              "reasoning": "These reviews contain [keywords] which indicates [domain] category"
            }
            
            DOMAIN DEFINITIONS and KEYWORDS:
            
            🍕 FOOD (Food/Beverage):
            - Keywords: taste, flavor, portion, burger, pizza, food, cold, hot, salty, sweet, spicy, ingredients, freshness, filling, raw, cooked, seasoning, sauce, meat, chicken, vegetables, fruits, drinks, water, tea, coffee, ice cream
            - Indicators: Food names, taste descriptions, temperature status, portion size
            
            🏪 RESTAURANT (Restaurant/Venue):
            - Keywords: restaurant, venue, atmosphere, environment, music, decor, cleanliness, waiter, table, reservation, location, parking, surroundings, service, waiting, menu, price list
            - Indicators: Venue features, staff service, physical environment
            
            🛒 E_COMMERCE (Product Shopping):
            - Keywords: product, order, delivery, shipping, package, box, packaging, seller, store, return, exchange, discount, campaign, payment, credit card, logistics
            - Indicators: Physical product features, shopping process, logistics
            
            👥 SERVICE (Service):
            - Keywords: service, support, customer, representative, solution, help, appointment, transaction, application, approval, rejection, waiting, response, professional, reliable
            - Indicators: Human interaction, problem solving, support process
            
            📱 APP (Application/Software):
            - Keywords: application, app, software, interface, button, screen, menu, feature, update, bug, error, slow, fast, easy, difficult, download, installation
            - Indicators: Technical terms, user interface, performance
            
            IMPORTANT: If multiple domain indicators exist, choose the DOMINANT one!
            
            Text to analyze: "$text"
        """.trimIndent()
    }

    private fun getDomainContext(domain: ReviewDomain): String {
        return when (domain) {
            ReviewDomain.E_COMMERCE -> "product quality, delivery experience, price-performance ratio and customer satisfaction"
            ReviewDomain.FOOD -> "flavor profile, freshness, portion size and overall dining experience"
            ReviewDomain.RESTAURANT -> "atmosphere, service quality, cleanliness and overall experience"
            ReviewDomain.SERVICE -> "customer support, service quality, reliability and satisfaction"
            ReviewDomain.APP -> "user interface, performance, features and overall usability"
            ReviewDomain.OTHER -> "general satisfaction and experience"
        }
    }
} 