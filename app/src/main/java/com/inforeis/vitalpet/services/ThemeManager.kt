package com.inforeis.vitalpet.services

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val storageKey: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorageKey(key: String?): AppThemeMode {
            return entries.find { it.storageKey == key } ?: SYSTEM
        }
    }
}

object ThemeManager {
    private const val PREFS_NAME = "vitalpet_prefs"
    private const val KEY_THEME = "app_theme_mode"

    private val _currentThemeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val currentThemeMode: StateFlow<AppThemeMode> = _currentThemeMode.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs?.getString(KEY_THEME, AppThemeMode.SYSTEM.storageKey)
        _currentThemeMode.value = AppThemeMode.fromStorageKey(savedTheme)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _currentThemeMode.value = mode
        prefs?.edit()?.putString(KEY_THEME, mode.storageKey)?.apply()
    }
}
