package com.inforeis.vitalpet.services

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
import java.util.prefs.Preferences

data class SupportedLanguage(
    val code: String,
    val name: String,
    val flag: String
)

object LanguageManager {
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

    private val prefs: Preferences by lazy {
        Preferences.userNodeForPackage(LanguageManager::class.java)
    }

    private val _currentLanguage = MutableStateFlow(DEFAULT_LANG)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    var composeLanguage: String by mutableStateOf(DEFAULT_LANG)
        private set

    private var translations: Map<String, Map<String, String>> = emptyMap()

    fun initialize() {
        val initialLang = detectInitialLanguage()
        val savedLang = prefs.get(KEY_LANG, initialLang) ?: initialLang
        _currentLanguage.value = savedLang
        composeLanguage = savedLang

        try {
            val resourceStream = javaClass.classLoader.getResourceAsStream("translations.json")
                ?: javaClass.getResourceAsStream("/translations.json")
            if (resourceStream != null) {
                resourceStream.use { stream ->
                    val reader = InputStreamReader(stream, "UTF-8")
                    val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
                    translations = Gson().fromJson(reader, type)
                }
            } else {
                System.err.println("VitalPet Desktop: translations.json not found in resources!")
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

    fun setLanguage(lang: String) {
        val validLang = if (SUPPORTED_LANGUAGES.any { it.code == lang }) lang else DEFAULT_LANG
        _currentLanguage.value = validLang
        composeLanguage = validLang
        try {
            prefs.put(KEY_LANG, validLang)
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
