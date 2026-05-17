package com.garam.mydream.core.settings

import android.content.Context
import android.os.Build
import com.garam.mydream.core.localization.AppLanguage

private const val PREFS_NAME = "mydream_app_settings"
private const val KEY_LANGUAGE = "key_language"
private const val KEY_THEME_MODE = "key_theme_mode"

class AndroidAppSettingsStorage(
    private val context: Context
) : AppSettingsStorage {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLanguageName(): String? = prefs.getString(KEY_LANGUAGE, null)

    override fun setLanguageName(value: String) {
        prefs.edit().putString(KEY_LANGUAGE, value).apply()
    }

    override fun getSystemLanguageName(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return AppLanguage.fromLanguageTag(locale.toLanguageTag()).name
    }

    override fun getThemeModeName(): String? = prefs.getString(KEY_THEME_MODE, null)

    override fun setThemeModeName(value: String) {
        prefs.edit().putString(KEY_THEME_MODE, value).apply()
    }
}
