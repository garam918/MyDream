package com.garam.mydream.core.settings

import android.content.Context

private const val PREFS_NAME = "mydream_app_settings"
private const val KEY_LANGUAGE = "key_language"
private const val KEY_THEME_MODE = "key_theme_mode"

class AndroidAppSettingsStorage(
    context: Context
) : AppSettingsStorage {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLanguageName(): String? = prefs.getString(KEY_LANGUAGE, null)

    override fun setLanguageName(value: String) {
        prefs.edit().putString(KEY_LANGUAGE, value).apply()
    }

    override fun getThemeModeName(): String? = prefs.getString(KEY_THEME_MODE, null)

    override fun setThemeModeName(value: String) {
        prefs.edit().putString(KEY_THEME_MODE, value).apply()
    }
}
