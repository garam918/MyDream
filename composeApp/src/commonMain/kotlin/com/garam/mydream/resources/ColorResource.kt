package com.garam.mydream.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object MyTheme {
    val colors: MyColors
        @Composable
        get() = LocalMyColors.current
}

data class MyColors(
    val mainColor: Color,
    val mainBackgroundColor: Color,
    val secondaryColor : Color,
    val onboardingBGColor : Color,
    val onboardingBtnBGColor : Color,
    val loginTopTextColor : Color,
    val loginExplainTextColor : Color,
    val textWhiteColor : Color,
    val calendarTextColor : Color,
    val btnTextColor : Color,
    val cardBgColor: Color,
    val dreamBtnColor: Color,
    val luckyItemCardBgColor : Color,
    val calendarSelectedDateBgColor : Color,
)

val LightColorPalette = MyColors(
    mainColor = Color(0xFF6200EE),
    mainBackgroundColor = Color(0xFFFDFBFF),
    secondaryColor = Color(0xFF000000),
    onboardingBGColor = Color.White,
    onboardingBtnBGColor = Color(0xFF6B4E96),
    loginTopTextColor = Color(0xFF111827),
    loginExplainTextColor = Color(0xFF6B7280),
    textWhiteColor = Color(0xFF0B0E20),
    calendarTextColor = Color(0xFF0B0E20),
    btnTextColor = Color(0xFFF5F5F7),
    cardBgColor = Color(0XFFF4F4F6),
    dreamBtnColor = Color(0xFF0B0E20),
    luckyItemCardBgColor = Color(0xFFF9FAFB),
    calendarSelectedDateBgColor = Color(0xFF000000),
)

// 3. 다크 모드 색상
val DarkColorPalette = MyColors(
    mainColor = Color(0xFF0B0E20),
    mainBackgroundColor = Color(0xFF0B0E20),
    secondaryColor = Color(0xFFEBC77D),
    onboardingBGColor = Color(0xFF0A0D1E),
    onboardingBtnBGColor = Color(0xFF6B4E96),
    loginTopTextColor = Color.White,
    loginExplainTextColor = Color(0xFF94A3B8),
    textWhiteColor = Color(0xFFF5F5F7),
    calendarTextColor = Color.White,
    btnTextColor = Color(0xFFF5F5F7),
    cardBgColor = Color(0XFF1C2137),
    dreamBtnColor = Color(0xFFEBC77D),
    luckyItemCardBgColor = Color(0xFF0B0E20),
    calendarSelectedDateBgColor = Color(0xFF6B4E96)
)

val LocalMyColors = staticCompositionLocalOf { LightColorPalette }