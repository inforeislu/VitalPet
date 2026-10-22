package com.inforeis.vitalpet.desktop.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.prefs.Preferences

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

object ThemeManager {
    private val prefs = Preferences.userNodeForPackage(ThemeManager::class.java)
    private const val PREF_KEY = "desktop_theme_mode"

    private val _themeMode = MutableStateFlow(
        runCatching {
            AppThemeMode.valueOf(prefs.get(PREF_KEY, AppThemeMode.DARK.name))
        }.getOrDefault(AppThemeMode.DARK)
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        runCatching {
            prefs.put(PREF_KEY, mode.name)
        }
    }
}
