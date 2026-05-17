@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.garam.mydream.core.localization

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.intl.Locale
import org.jetbrains.compose.resources.ComposeEnvironment
import org.jetbrains.compose.resources.DensityQualifier
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.RegionQualifier
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.ThemeQualifier

enum class AppLanguage(val languageTag: String) {
    KOREAN("ko"),
    ENGLISH("en");

    companion object {
        fun fromLanguageTag(languageTag: String?): AppLanguage {
            val normalizedTag = languageTag
                ?.replace('_', '-')
                ?.lowercase()
                ?: return ENGLISH

            return entries.firstOrNull { language ->
                normalizedTag == language.languageTag ||
                    normalizedTag.startsWith("${language.languageTag}-")
            } ?: ENGLISH
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@OptIn(InternalResourceApi::class)
@Composable
fun AppLanguageProvider(
    appLanguage: AppLanguage,
    content: @Composable () -> Unit
) {
    val systemLocale = Locale.current
    val isDarkTheme = isSystemInDarkTheme()
    val density = LocalDensity.current

    val composeEnvironment = remember(appLanguage, systemLocale.region, isDarkTheme, density) {
        object : ComposeEnvironment {
            @Composable
            override fun rememberEnvironment(): ResourceEnvironment {
                return remember(appLanguage, systemLocale.region, isDarkTheme, density) {
                    ResourceEnvironment(
                        language = LanguageQualifier(appLanguage.languageTag),
                        region = RegionQualifier(systemLocale.region),
                        theme = ThemeQualifier.selectByValue(isDarkTheme),
                        density = DensityQualifier.selectByDensity(density.density)
                    )
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides appLanguage,
        LocalComposeEnvironment provides composeEnvironment
    ) {
        content()
    }
}
