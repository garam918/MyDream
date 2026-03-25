package com.garam.mydream.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.garam.mydream.resources.MyTheme
import com.garam.mydream.resources.fontFamily
import com.garam.mydream.util.dayOfWeekToShortText
import com.garam.mydream.util.localDateToMonthText
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.common_confirm
import mydream.composeapp.generated.resources.common_cancel
import mydream.composeapp.generated.resources.date_picker_title
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
@Composable
fun DateSelectDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var tempSelectedDate by remember(selectedDate) { mutableStateOf(selectedDate) }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY) }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = selectedDate.yearMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    val scope = rememberCoroutineScope()
    val visibleMonth = calendarState.firstVisibleMonth.yearMonth

    LaunchedEffect(selectedDate) {
        calendarState.scrollToMonth(selectedDate.yearMonth)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = MyTheme.colors.textWhiteColor
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.date_picker_title),
                    color = MyTheme.colors.textWhiteColor,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MonthMoveButton(text = "<") {
                        scope.launch {
                            calendarState.animateScrollToMonth(visibleMonth.minusMonths(1))
                        }
                    }

                    Text(
                        text = localDateToMonthText(
                            LocalDate(visibleMonth.year, visibleMonth.month, 1)
                        ),
                        color = MyTheme.colors.textWhiteColor,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    )

                    MonthMoveButton(text = ">") {
                        scope.launch {
                            calendarState.animateScrollToMonth(visibleMonth.plusMonths(1))
                        }
                    }
                }

                DaysOfWeekTitle(daysOfWeek = daysOfWeek)

                HorizontalCalendar(
                    state = calendarState,
                    dayContent = { day ->
                        DateDialogDay(
                            day = day,
                            selectedDate = tempSelectedDate,
                            onClick = {
                                tempSelectedDate = day.date
                            }
                        )
                    },
                    monthHeader = { }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(Res.string.common_cancel),
                        color = Color(0xFF6B7280),
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )

                    Text(
                        text = stringResource(Res.string.common_confirm),
                        color = Color(0xFF6B4E96),
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable {
                                onDateSelected(tempSelectedDate)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthMoveButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color(0xFFF4F4F6), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MyTheme.colors.textWhiteColor,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun DateDialogDay(
    day: CalendarDay,
    selectedDate: LocalDate,
    onClick: () -> Unit
) {
    val enabled = day.position == com.kizitonwose.calendar.core.DayPosition.MonthDate

    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .size(36.dp)
            .background(
                color = if (day.date == selectedDate && enabled) Color(0xFF6B4E96) else Color.Transparent,
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.day.toString(),
            color = if (!enabled) {
                Color.Transparent
            } else if (day.date == selectedDate) {
                Color.White
            } else if (day.date.dayOfWeek == DayOfWeek.SUNDAY) {
                Color(0xFFE5484D)
            } else if (day.date.dayOfWeek == DayOfWeek.SATURDAY) {
                Color(0xFF4B83F5)
            } else {
                MyTheme.colors.textWhiteColor
            },
            textAlign = TextAlign.Center,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                text = when (dayOfWeek) {
                    DayOfWeek.SUNDAY -> dayOfWeekToShortText(DayOfWeek.SUNDAY)
                    DayOfWeek.MONDAY -> dayOfWeekToShortText(DayOfWeek.MONDAY)
                    DayOfWeek.TUESDAY -> dayOfWeekToShortText(DayOfWeek.TUESDAY)
                    DayOfWeek.WEDNESDAY -> dayOfWeekToShortText(DayOfWeek.WEDNESDAY)
                    DayOfWeek.THURSDAY -> dayOfWeekToShortText(DayOfWeek.THURSDAY)
                    DayOfWeek.FRIDAY -> dayOfWeekToShortText(DayOfWeek.FRIDAY)
                    DayOfWeek.SATURDAY -> dayOfWeekToShortText(DayOfWeek.SATURDAY)
                    else -> ""
                },
                textAlign = TextAlign.Center,
                color = Color(0xFF9CA3AF),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }
}
