package com.garam.mydream.core.settings

import com.garam.mydream.core.localization.AppLanguage
import platform.Foundation.NSUserDefaults

private const val KEY_LANGUAGE = "key_language"
private const val KEY_THEME_MODE = "key_theme_mode"

class IosAppSettingsStorage : AppSettingsStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun getLanguageName(): String? = userDefaults.stringForKey(KEY_LANGUAGE)

    override fun setLanguageName(value: String) {
        userDefaults.setObject(value, forKey = KEY_LANGUAGE)
    }

    override fun getSystemLanguageName(): String {
        val languageTag = userDefaults.stringArrayForKey("AppleLanguages")?.firstOrNull() as? String
        return AppLanguage.fromLanguageTag(languageTag).name
    }

    override fun getThemeModeName(): String? = userDefaults.stringForKey(KEY_THEME_MODE)

    override fun setThemeModeName(value: String) {
        userDefaults.setObject(value, forKey = KEY_THEME_MODE)
    }
}
