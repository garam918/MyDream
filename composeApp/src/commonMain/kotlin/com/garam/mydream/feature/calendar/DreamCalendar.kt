package com.garam.mydream.feature.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.localization.AppLanguage
import com.garam.mydream.core.localization.LocalAppLanguage
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import com.garam.mydream.core.util.dayOfWeekToShortText
import com.garam.mydream.core.util.localDateToDateText
import com.garam.mydream.core.util.localDateToMonthText
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.calendar_good_dream_label
import mydream.composeapp.generated.resources.calendar_bad_dream_label
import mydream.composeapp.generated.resources.calendar_delete_button
import mydream.composeapp.generated.resources.calendar_delete_dialog_description
import mydream.composeapp.generated.resources.calendar_delete_dialog_title
import mydream.composeapp.generated.resources.calendar_delete_failed_message
import mydream.composeapp.generated.resources.calendar_today_button
import mydream.composeapp.generated.resources.common_cancel
import mydream.composeapp.generated.resources.common_confirm
import mydream.composeapp.generated.resources.common_notice
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime


// 캘린더 기능
@OptIn(ExperimentalTime::class)
@Composable
fun DreamCalendar(
    onNavigateToDreamInterpretation: (DreamResponse) -> Unit,
    calendarViewModel: CalendarViewModel = koinViewModel()
) {

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val dreamContentList = calendarViewModel.dreamContentList.collectAsState()
    val dreamDeleteState by calendarViewModel.dreamDeleteState.collectAsState()

    val selectedDateDreamList = dreamContentList.value.filter { it.analysisDate == selectedDate.toString() }
    val dreamMarkerMap = dreamContentList.value
        .groupBy { it.analysisDate }
        .mapValues { (_, dreams) -> dreams.resolveDreamMarker() }

    var selectedDreamAnalysis by remember { mutableStateOf<DreamAnalysisEntity?>(null) }
    var dreamPendingDelete by remember { mutableStateOf<DreamAnalysisEntity?>(null) }

    if (dreamPendingDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (dreamDeleteState.deletingDreamId == null) {
                    dreamPendingDelete = null
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.calendar_delete_dialog_title),
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    color = MyTheme.colors.textWhiteColor
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.calendar_delete_dialog_description),
                    fontFamily = fontFamily(),
                    color = MyTheme.colors.textWhiteColor.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dreamPendingDelete?.id?.let(calendarViewModel::deleteDream)
                    },
                    enabled = dreamDeleteState.deletingDreamId == null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5484D))
                ) {
                    Text(
                        text = stringResource(Res.string.calendar_delete_button),
                        fontFamily = fontFamily()
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        dreamPendingDelete = null
                    },
                    enabled = dreamDeleteState.deletingDreamId == null
                ) {
                    Text(
                        text = stringResource(Res.string.common_cancel),
                        fontFamily = fontFamily()
                    )
                }
            },
            containerColor = MyTheme.colors.cardBgColor
        )
    }

    if (dreamDeleteState.lastDeleteFailed) {
        AlertDialog(
            onDismissRequest = {
                calendarViewModel.clearDeleteFailure()
            },
            title = {
                Text(
                    text = stringResource(Res.string.common_notice),
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    color = MyTheme.colors.textWhiteColor
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.calendar_delete_failed_message),
                    fontFamily = fontFamily(),
                    color = MyTheme.colors.textWhiteColor.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        calendarViewModel.clearDeleteFailure()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.common_confirm),
                        fontFamily = fontFamily()
                    )
                }
            },
            containerColor = MyTheme.colors.cardBgColor
        )
    }

    LaunchedEffect(dreamDeleteState.deletingDreamId) {
        if (dreamDeleteState.deletingDreamId == null && dreamPendingDelete != null && !dreamDeleteState.lastDeleteFailed) {
            dreamPendingDelete = null
        }
    }


    LazyColumn(
        modifier = Modifier.background(color = MyTheme.colors.mainBackgroundColor).fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        item {

            Column(modifier = Modifier.background(color = MyTheme.colors.mainBackgroundColor)) {

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = localDateToMonthText(selectedDate),
                        color = MyTheme.colors.textWhiteColor,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedButton(
                        onClick = {
                            selectedDate = LocalDate.now()
                        },
                        enabled = selectedDate != LocalDate.now(),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.calendar_today_button),
                            fontFamily = fontFamily(),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                DrawCalendar(selectedDate, onClick = {
                    selectedDate = it.date
                },
                    dreamMarkerMap = dreamMarkerMap
                    , onMonthScroll = {

                    selectedDate = if (it == LocalDate.now().yearMonth) LocalDate.now() else it.firstDay
                })

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(), thickness = 1.dp,
                    color = Color.LightGray
                )
            }


        }

        item {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = localDateToDateText(selectedDate),
                color = MyTheme.colors.textWhiteColor,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(selectedDateDreamList) { item ->

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    contentColor = MyTheme.colors.mainBackgroundColor,
                    containerColor = MyTheme.colors.mainBackgroundColor
                ),
                modifier = Modifier.clickable(enabled = true, onClick = {

                    selectedDreamAnalysis = item
                    onNavigateToDreamInterpretation(item.toDreamResponse())

                    // 꿈 분석 완료 화면으로 이동
                })
            ) {

                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                    Row {
                        dreamListLabel(item.score)?.let { label ->
                            val goodDreamLabel = stringResource(Res.string.calendar_good_dream_label)
                            Row(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (label == goodDreamLabel) Color(0xFFD7E7FF) else Color(0xFFFFD7D7),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .background(
                                        color = if (label == goodDreamLabel) Color(0xFFF4F8FF) else Color(0xFFFFF5F5),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {

                                Text(
                                    text = label,
                                    fontFamily = fontFamily(),
                                    color = if (label == goodDreamLabel) Color(0xFF3E7BFA) else Color(0xFFE5484D),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = stringResource(Res.string.calendar_delete_button),
                            fontFamily = fontFamily(),
                            color = Color(0xFFE5484D),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable(
                                enabled = dreamDeleteState.deletingDreamId == null,
                                onClick = {
                                    dreamPendingDelete = item
                                }
                            )
                        )


                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.title,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Bold,
                        color = MyTheme.colors.textWhiteColor,
                        fontSize = 18.sp
                    ) // 꿈 제목 요약

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = item.analysis, maxLines = 2,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Normal,
                        color = MyTheme.colors.textWhiteColor,
                        fontSize = 15.sp) // 꿈 내용 상세 내용 글자수 제한해서

                }
            }

        }


    }


}

private fun DreamAnalysisEntity.toDreamResponse(): DreamResponse {
    return DreamResponse(
        title = title,
        score = score,
        analysis = analysis,
        energy_label = energy_label,
        energy_percent = energy_percent,
        good_points = good_points,
        warn_points = warn_points,
        lucky_item = lucky_item,
        lucky_color = lucky_color
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun DrawCalendar(
    selectedDate: LocalDate,
    dreamMarkerMap : Map<String, DreamMarker>,
    onClick: (CalendarDay) -> Unit,
    onMonthScroll: (YearMonth) -> Unit
) {

    val currentMonth = remember { YearMonth.now() }

    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }

    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)

    val monthCalendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(monthCalendarState) {

        snapshotFlow { monthCalendarState.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collect {

                onMonthScroll(it)

            }
    }

    LaunchedEffect(selectedDate) {

        monthCalendarState.scrollToMonth(selectedDate.yearMonth)

    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalCalendar(
            state = monthCalendarState,
            dayContent = { day ->
                DrawDay(day, onClick = {
                    onClick(day)
                }, selectedDate, dreamMarker = dreamMarkerMap[day.date.toString()])
            },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek)

            },
            userScrollEnabled = true,


            )
    }
}

@Composable
private fun DrawDay(
    day: CalendarDay,
    onClick: (CalendarDay) -> Unit,
    selectedDate: LocalDate,
    dreamMarker: DreamMarker?
) {
    val dreamMarkerColor = dreamMarker?.color()

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (day.position == DayPosition.MonthDate) {
            Box(
                modifier = Modifier.background(
                    color = if (day.date == selectedDate) MyTheme.colors.calendarSelectedDateBgColor else Color.Unspecified,
                    if (day.date == selectedDate) CircleShape else ShapeDefaults.Small
                ).size(24.dp),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.day.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (day.date == selectedDate) Color.White else if(day.date.dayOfWeek == DayOfWeek.SUNDAY) Color.Red else if(day.date.dayOfWeek == DayOfWeek.SATURDAY) Color.Blue else MyTheme.colors.calendarTextColor,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = fontFamily(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier.height(6.dp),
                contentAlignment = Alignment.Center
            ) {
                dreamMarkerColor?.let {
                    Canvas(modifier = Modifier.size(4.dp)) {
                        drawCircle(
                            color = it,
                            radius = size.minDimension / 2
                        )
                    }
                }
            }
        }
    }


}

private enum class DreamMarker {
    GOOD,
    NORMAL,
    BAD;

    @Composable
    fun color(): Color {
        return when (this) {
            GOOD -> MyTheme.colors.calendarGoodDreamMarkerColor
            NORMAL -> MyTheme.colors.calendarNormalDreamMarkerColor
            BAD -> MyTheme.colors.calendarBadDreamMarkerColor
        }
    }
}

private fun Int.toDreamMarker(): DreamMarker {
    return when {
        this >= 80 -> DreamMarker.GOOD
        this < 60 -> DreamMarker.BAD
        else -> DreamMarker.NORMAL
    }
}

private fun List<DreamAnalysisEntity>.resolveDreamMarker(): DreamMarker {
    val markers = map { it.score.toDreamMarker() }.toSet()
    return if (markers.size >= 2) {
        DreamMarker.GOOD
    } else {
        markers.first()
    }
}

@Composable
private fun dreamListLabel(score: Int): String? {
    return when {
        score >= 80 -> stringResource(Res.string.calendar_good_dream_label)
        score < 60 -> stringResource(Res.string.calendar_bad_dream_label)
        else -> null
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = if (LocalAppLanguage.current == AppLanguage.KOREAN) {
                    dayOfWeekToShortText(dayOfWeek)
                } else {
                    dayOfWeekToShortText(dayOfWeek).take(1)
                },
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
