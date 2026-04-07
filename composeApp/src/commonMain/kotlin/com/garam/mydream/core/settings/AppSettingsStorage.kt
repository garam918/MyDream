package com.garam.mydream.core.settings

interface AppSettingsStorage {
    fun getLanguageName(): String?
    fun setLanguageName(value: String)
    fun getThemeModeName(): String?
    fun setThemeModeName(value: String)
}
