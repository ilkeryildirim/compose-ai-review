package com.github.ilkeryildirim.aireviewcompose.sample.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.*

/**
 * Modern Locale Manager with Compose CompositionLocal support
 */
object LocaleManager {
    
    private const val LANGUAGE_PREF = "language_pref"
    private const val LANGUAGE_KEY = "language_key"
    private const val INIT_STATE_KEY = "init_state_key"
    
    /**
     * Set application language using AppCompatDelegate (Best Practice)
     */
    fun setAppLanguage(languageCode: String) {
        try {
            val appLocale = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } catch (e: Exception) {
            // Fallback for older versions
            android.util.Log.w("LocaleManager", "AppCompatDelegate failed, using fallback")
        }
    }
    
    /**
     * Get current app language
     */
    fun getCurrentLanguage(context: Context): String {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        return if (currentLocales.isEmpty) {
            getLanguage(context)
        } else {
            currentLocales[0]?.language ?: "tr"
        }
    }
    
    /**
     * Set application language (Legacy support)
     */
    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }
    
    /**
     * Get saved language or default
     */
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "tr") ?: "tr"
    }
    
    /**
     * Save language preference
     */
    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }
    
    /**
     * Save initialization state
     */
    fun saveInitState(context: Context, isInitialized: Boolean) {
        val prefs = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(INIT_STATE_KEY, isInitialized).apply()
    }
    
    /**
     * Get saved initialization state
     */
    fun getInitState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        return prefs.getBoolean(INIT_STATE_KEY, false)
    }
    
    /**
     * Update context resources with new locale
     */
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
    
    /**
     * Get available languages
     */
    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "tr" to "Türkçe",
            "en" to "English"
        )
    }
}

/**
 * CompositionLocal for current app language
 */
val LocalAppLanguage = compositionLocalOf { "tr" }

/**
 * Composable for managing app locale state without restart
 */
@Composable
fun rememberLocaleState(context: Context): MutableState<String> {
    return remember { 
        mutableStateOf(LocaleManager.getCurrentLanguage(context))
    }
}

/**
 * App Language Provider - no restart needed!
 */
@Composable
fun AppLanguageProvider(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppLanguage provides currentLanguage
    ) {
        // Update system language when composition language changes
        LaunchedEffect(currentLanguage) {
            LocaleManager.setAppLanguage(currentLanguage)
        }
        
        content()
    }
}

/**
 * Simple effect for handling language changes
 */
@Composable
fun LanguageChangeEffect(
    targetLanguage: String,
    onLanguageChanged: (String) -> Unit = {}
) {
    LaunchedEffect(targetLanguage) {
        LocaleManager.setAppLanguage(targetLanguage)
        onLanguageChanged(targetLanguage)
    }
} 