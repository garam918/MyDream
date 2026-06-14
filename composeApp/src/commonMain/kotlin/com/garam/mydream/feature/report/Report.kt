package com.garam.mydream.feature.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.core.data.model.DreamReport
import com.garam.mydream.core.data.model.ReportFrequency
import com.garam.mydream.core.data.model.ReportScorePoint
import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import kotlinx.datetime.LocalDate
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.report_bad_dream
import mydream.composeapp.generated.resources.report_color
import mydream.composeapp.generated.resources.report_count
import mydream.composeapp.generated.resources.report_dream_count
import mydream.composeapp.generated.resources.report_empty
import mydream.composeapp.generated.resources.report_error
import mydream.composeapp.generated.resources.report_good_dream
import mydream.composeapp.generated.resources.report_item
import mydream.composeapp.generated.resources.report_lucky_color
import mydream.composeapp.generated.resources.report_lucky_item
import mydream.composeapp.generated.resources.report_monthly
import mydream.composeapp.generated.resources.report_ratio
import mydream.composeapp.generated.resources.report_retry
import mydream.composeapp.generated.resources.report_score_graph
import mydream.composeapp.generated.resources.report_weekly
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Report(
    viewModel: ReportViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTheme.colors.mainBackgroundColor)
    ) {
        ReportTypeSelector(
            selectedType = state.selectedType,
            onSelect = viewModel::selectType
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MyTheme.colors.mainColor)
            }
            state.hasError -> ReportMessage(
                message = stringResource(Res.string.report_error),
                buttonText = stringResource(Res.string.report_retry),
                onClick = viewModel::refresh
            )
            state.report != null -> ReportContent(state.report!!)
        }
    }
}

@Composable
private fun ReportTypeSelector(
    selectedType: ReportType,
    onSelect: (ReportType) -> Unit
) {
    val isDarkTheme = MyTheme.colors.mainColor == MyTheme.colors.mainBackgroundColor
    val selectedContainerColor = if (isDarkTheme) {
        MyTheme.colors.secondaryColor
    } else {
        MyTheme.colors.secondaryColor
    }
    val selectedContentColor = if (isDarkTheme) {
        MyTheme.colors.mainBackgroundColor
    } else {
        Color.White
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportType.entries.forEach { type ->
            val selected = selectedType == type
            Button(
                onClick = { onSelect(type) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) selectedContainerColor else MyTheme.colors.cardBgColor,
                    contentColor = if (selected) selectedContentColor else MyTheme.colors.textWhiteColor
                )
            ) {
                Text(
                    text = stringResource(
                        if (type == ReportType.WEEKLY) Res.string.report_weekly
                        else Res.string.report_monthly
                    ),
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ReportContent(report: DreamReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = formatPeriod(report.periodStart, report.periodEnd),
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        if (report.dreamCount == 0) {
            item {
                ReportCard {
                    Text(
                        text = stringResource(Res.string.report_empty),
                        color = MyTheme.colors.textWhiteColor,
                        fontFamily = fontFamily(),
                        modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            item { ScoreGraphCard(report.scorePoints) }
            item { DreamRatioCard(report) }
            item {
                FrequencyTable(
                    title = stringResource(Res.string.report_lucky_color),
                    nameHeader = stringResource(Res.string.report_color),
                    rows = report.luckyColors
                )
            }
            item {
                FrequencyTable(
                    title = stringResource(Res.string.report_lucky_item),
                    nameHeader = stringResource(Res.string.report_item),
                    rows = report.luckyItems
                )
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ScoreGraphCard(points: List<ReportScorePoint>) {
    ReportCard {
        SectionTitle(stringResource(Res.string.report_score_graph))
        Spacer(Modifier.height(16.dp))
        ScoreGraph(points)
    }
}

@Composable
private fun ScoreGraph(points: List<ReportScorePoint>) {
    val lineColor = MyTheme.colors.mainColor
    val gridColor = MyTheme.colors.textWhiteColor.copy(alpha = 0.15f)
    val labelStep = if (points.size <= 7) 1 else 5

    Column {
        Canvas(Modifier.fillMaxWidth().height(180.dp)) {
            val topPadding = 10.dp.toPx()
            val bottomPadding = 18.dp.toPx()
            val graphHeight = size.height - topPadding - bottomPadding
            val spacing = if (points.size <= 1) 0f else size.width / (points.size - 1)

            listOf(0, 25, 50, 75, 100).forEach { score ->
                val y = topPadding + graphHeight * (1f - score / 100f)
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            }

            val segments = mutableListOf<Path>()
            var path: Path? = null
            points.forEachIndexed { index, point ->
                val score = point.score
                if (score == null) {
                    path?.let(segments::add)
                    path = null
                } else {
                    val x = index * spacing
                    val y = topPadding + graphHeight * (1f - score.coerceIn(0, 100) / 100f)
                    if (path == null) path = Path().apply { moveTo(x, y) } else path.lineTo(x, y)
                    drawCircle(lineColor, 3.5.dp.toPx(), Offset(x, y))
                }
            }
            path?.let(segments::add)
            segments.forEach { drawPath(it, lineColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)) }
        }
        Row(Modifier.fillMaxWidth()) {
            points.forEachIndexed { index, point ->
                Text(
                    text = if (index % labelStep == 0 || index == points.lastIndex) {
                        LocalDate.parse(point.date).day.toString()
                    } else "",
                    modifier = Modifier.weight(1f),
                    color = MyTheme.colors.textWhiteColor.copy(alpha = 0.65f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DreamRatioCard(report: DreamReport) {
    val total = report.goodDreamCount + report.badDreamCount
    val goodPercent = if (total == 0) 0 else report.goodDreamCount * 100 / total
    val badPercent = if (total == 0) 0 else 100 - goodPercent

    ReportCard {
        SectionTitle(stringResource(Res.string.report_ratio))
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            RatioItem(stringResource(Res.string.report_good_dream), report.goodDreamCount, goodPercent, Color(0xFF3E7BFA))
            RatioItem(stringResource(Res.string.report_bad_dream), report.badDreamCount, badPercent, Color(0xFFE5484D))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.report_dream_count, report.dreamCount),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MyTheme.colors.textWhiteColor.copy(alpha = 0.7f),
            fontFamily = fontFamily(),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun RatioItem(label: String, count: Int, percent: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(12.dp)) { drawCircle(color) }
        Spacer(Modifier.height(6.dp))
        Text("$percent%", color = MyTheme.colors.textWhiteColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("$label · $count", color = MyTheme.colors.textWhiteColor.copy(alpha = 0.7f), fontFamily = fontFamily())
    }
}

@Composable
private fun FrequencyTable(title: String, nameHeader: String, rows: List<ReportFrequency>) {
    ReportCard {
        SectionTitle(title)
        Spacer(Modifier.height(12.dp))
        TableRow(nameHeader, stringResource(Res.string.report_count), header = true)
        rows.forEach { row ->
            HorizontalDivider(color = MyTheme.colors.textWhiteColor.copy(alpha = 0.08f))
            TableRow(row.name, row.count.toString())
        }
    }
}

@Composable
private fun TableRow(name: String, count: String, header: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(name, Modifier.weight(1f), color = MyTheme.colors.textWhiteColor, fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal, fontFamily = fontFamily())
        Text(count, color = MyTheme.colors.textWhiteColor.copy(alpha = if (header) 1f else 0.75f), fontFamily = fontFamily())
    }
}

@Composable
private fun ReportCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MyTheme.colors.cardBgColor)
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = MyTheme.colors.textWhiteColor, fontFamily = fontFamily(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
}

@Composable
private fun ReportMessage(message: String, buttonText: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MyTheme.colors.textWhiteColor, fontFamily = fontFamily())
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick) { Text(buttonText) }
        }
    }
}

private fun formatPeriod(start: String, end: String): String {
    return "$start ~ $end"
}
