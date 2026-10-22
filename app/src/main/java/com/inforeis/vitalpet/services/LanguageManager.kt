package com.inforeis.vitalpet.services

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStreamReader
import java.util.Locale

data class SupportedLanguage(
    val code: String,
    val name: String,
    val flag: String
)

object LanguageManager {
    private const val PREFS_NAME = "vitalpet_prefs"
    private const val KEY_LANG = "app_language"
    private const val DEFAULT_LANG = "pt"

    val SUPPORTED_LANGUAGES = listOf(
        SupportedLanguage("pt", "Português", "🇵🇹"),
        SupportedLanguage("en", "English", "🇺🇸"),
        SupportedLanguage("es", "Español", "🇪🇸"),
        SupportedLanguage("fr", "Français", "🇫🇷"),
        SupportedLanguage("de", "Deutsch", "🇩🇪"),
        SupportedLanguage("it", "Italiano", "🇮🇹"),
        SupportedLanguage("nl", "Nederlands", "🇳🇱"),
        SupportedLanguage("lb", "Lëtzebuergesch", "🇱🇺")
    )

    private val _currentLanguage = MutableStateFlow(DEFAULT_LANG)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Observable Compose state - when this changes, all composables reading .localized() recompose automatically
    var composeLanguage: String by mutableStateOf(DEFAULT_LANG)
        private set

    private var translations: Map<String, Map<String, String>> = emptyMap()
    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val initialLang = detectInitialLanguage()
        val savedLang = prefs?.getString(KEY_LANG, initialLang) ?: initialLang
        _currentLanguage.value = savedLang
        composeLanguage = savedLang
        applyLocaleToSystem(context, savedLang)

        try {
            context.assets.open("translations.json").use { stream ->
                val reader = InputStreamReader(stream, "UTF-8")
                val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
                translations = Gson().fromJson(reader, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detectInitialLanguage(): String {
        val deviceLanguage = Locale.getDefault().language.lowercase()
        return if (SUPPORTED_LANGUAGES.any { it.code == deviceLanguage }) {
            deviceLanguage
        } else {
            DEFAULT_LANG
        }
    }

    fun setLanguage(lang: String, context: Context? = null) {
        val validLang = if (SUPPORTED_LANGUAGES.any { it.code == lang }) lang else DEFAULT_LANG
        _currentLanguage.value = validLang
        composeLanguage = validLang
        prefs?.edit()?.putString(KEY_LANG, validLang)?.apply()

        val targetContext = context ?: appContext
        targetContext?.let { applyLocaleToSystem(it, validLang) }
    }

    private fun applyLocaleToSystem(context: Context, langCode: String) {
        try {
            val locale = when (langCode) {
                "lb" -> Locale("lb", "LU")
                "pt" -> Locale("pt", "PT")
                "en" -> Locale("en", "US")
                "es" -> Locale("es", "ES")
                "fr" -> Locale("fr", "FR")
                "de" -> Locale("de", "DE")
                "it" -> Locale("it", "IT")
                "nl" -> Locale("nl", "NL")
                else -> Locale(langCode)
            }
            Locale.setDefault(locale)
            val resources = context.resources
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun get(key: String, lang: String = composeLanguage): String {
        val entry = translations[key] ?: return key
        return entry[lang] ?: entry[DEFAULT_LANG] ?: entry["en"] ?: key
    }

    fun String.localized(): String = get(this, composeLanguage)
    fun String.localized(lang: String): String = get(this, lang)
}
