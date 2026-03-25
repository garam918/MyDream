package com.garam.mydream.util

import androidx.compose.runtime.Composable
import com.garam.mydream.localization.AppLanguage
import com.garam.mydream.localization.LocalAppLanguage
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun localDateToText(date: LocalDate): String {
    return when (LocalAppLanguage.current) {
        AppLanguage.KOREAN -> "${date.year}년 ${date.month.number}월 ${date.day}일 (${dayOfWeekToShortText(date.dayOfWeek)})"
        AppLanguage.ENGLISH -> "${monthName(date.month.number)} ${date.day}, ${date.year} (${dayOfWeekToShortText(date.dayOfWeek)})"
    }
}

@Composable
fun localDateToMonthText(date: LocalDate): String {
    return when (LocalAppLanguage.current) {
        AppLanguage.KOREAN -> "${date.year}년 ${date.month.number}월"
        AppLanguage.ENGLISH -> "${monthName(date.month.number)} ${date.year}"
    }
}

@Composable
fun localDateToDateText(date: LocalDate): String {
    return when (LocalAppLanguage.current) {
        AppLanguage.KOREAN -> "${date.month.number}월 ${date.day}일 (${dayOfWeekToShortText(date.dayOfWeek)})"
        AppLanguage.ENGLISH -> "${monthName(date.month.number)} ${date.day} (${dayOfWeekToShortText(date.dayOfWeek)})"
    }
}

@Composable
fun dayOfWeekToShortText(dayOfWeek: DayOfWeek): String {
    return when (LocalAppLanguage.current) {
        AppLanguage.KOREAN -> when (dayOfWeek) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }

        AppLanguage.ENGLISH -> when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
        }
    }
}

private fun monthName(month: Int): String = when (month) {
    1 -> "January"
    2 -> "February"
    3 -> "March"
    4 -> "April"
    5 -> "May"
    6 -> "June"
    7 -> "July"
    8 -> "August"
    9 -> "September"
    10 -> "October"
    11 -> "November"
    12 -> "December"
    else -> month.toString()
}
