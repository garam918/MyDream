package com.garam.mydream.ui.dreamInterpretation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.data.remote.DreamResponse
import com.garam.mydream.resources.MyTheme
import com.garam.mydream.resources.fontFamily
import mydream.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val ScreenBackground = Color(0xFFFDF9F4)
private val CardBorder = Color(0xFFE5E7EB)
private val CardShadow = Color(0x140B0E20)
private val PrimaryText = Color(0xFF151A2D)
private val SecondaryText = Color(0xFF6D748B)
private val Purple = Color(0xFF6E54A3)
private val PurpleSoft = Color(0xFFF2EBFF)
private val Pink = Color(0xFFF7A3AF)
private val Gold = Color(0xFFF0C261)
private val BlueSoft = Color(0xFFF4F8FF)
private val BlueBorder = Color(0xFFD3E3FF)
private val RedSoft = Color(0xFFFFF6F5)
private val RedBorder = Color(0xFFFFD9D4)
private val GoldSoft = Color(0xFFFFF7E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamInterpretation(
    dreamResponse: DreamResponse,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.dream_interpretation_screen_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily(),
                        color = PrimaryText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(Res.drawable.back_btn_icon),
                            contentDescription = stringResource(Res.string.common_back),
                            tint = PrimaryText
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = PrimaryText,
                    navigationIconContentColor = PrimaryText,
                    actionIconContentColor = PrimaryText,
                    scrolledContainerColor = ScreenBackground,
                    subtitleContentColor = PrimaryText
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = ScreenBackground,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HorizontalDivider(color = CardBorder.copy(alpha = 0.9f))
            }

            item {
                AnalysisCard(dreamResponse = dreamResponse)
            }

            item {
                ScoreCard(score = dreamResponse.score)
            }

            item {
                InsightRow(
                    goodPoints = dreamResponse.good_points,
                    warnPoints = dreamResponse.warn_points
                )
            }

            item {
                LuckyItemCard(
                    luckyItem = dreamResponse.lucky_item,
                    luckyColor = dreamResponse.lucky_color
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun AnalysisCard(dreamResponse: DreamResponse) {
    SurfaceCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PurpleSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI",
                        color = Purple,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = stringResource(Res.string.dream_interpretation_analysis_title),
                    color = PrimaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = dreamResponse.analysis.formatAnalysis(),
                color = SecondaryText,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 31.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.dream_interpretation_energy_negative),
                    color = SecondaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Text(
                    text = stringResource(
                        Res.string.dream_interpretation_energy_score,
                        dreamResponse.energy_percent.coerceIn(0, 100)
                    ),
                    modifier = Modifier.weight(1f),
                    color = Purple,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(Res.string.dream_interpretation_energy_positive),
                    color = SecondaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            EnergyBar(energyPercent = dreamResponse.energy_percent)
        }
    }
}

@Composable
private fun ScoreCard(score: Int) {
    SurfaceCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.dream_interpretation_fortune_score_title),
                    color = SecondaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${score.coerceIn(0, 100)}",
                        color = PrimaryText,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        lineHeight = 48.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(Res.string.dream_interpretation_score_unit),
                        color = Gold,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 7.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = buildStars(score),
                    color = Gold,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            }

            FortuneBadge()
        }
    }
}

@Composable
private fun FortuneBadge() {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp)
                .border(
                    width = 6.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Gold.copy(alpha = 0.15f),
                            Gold,
                            Gold.copy(alpha = 0.15f),
                            Gold
                        )
                    ),
                    shape = CircleShape
                )
        )

        Text(
            text = "✦",
            color = Gold,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InsightRow(
    goodPoints: List<String>,
    warnPoints: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        InsightCard(
            modifier = Modifier.weight(1f),
            title = stringResource(Res.string.dream_interpretation_expect_title),
            background = BlueSoft,
            border = BlueBorder,
            icon = "✦",
            iconColor = Color(0xFF4B83F5),
            bulletColor = Color(0xFF4B83F5),
            points = goodPoints.ifEmpty {
                listOf(
                    stringResource(Res.string.dream_interpretation_expect_fallback_1),
                    stringResource(Res.string.dream_interpretation_expect_fallback_2)
                )
            }
        )

        InsightCard(
            modifier = Modifier.weight(1f),
            title = stringResource(Res.string.dream_interpretation_caution_title),
            background = RedSoft,
            border = RedBorder,
            icon = "!",
            iconColor = Color(0xFFF04D48),
            bulletColor = Color(0xFFF04D48),
            points = warnPoints.ifEmpty {
                listOf(
                    stringResource(Res.string.dream_interpretation_caution_fallback_1),
                    stringResource(Res.string.dream_interpretation_caution_fallback_2)
                )
            }
        )
    }
}

@Composable
private fun InsightCard(
    modifier: Modifier = Modifier,
    title: String,
    background: Color,
    border: Color,
    icon: String,
    iconColor: Color,
    bulletColor: Color,
    points: List<String>
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(background)
            .border(BorderStroke(1.dp, border), RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                color = iconColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                color = PrimaryText,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        points.take(2).forEach { point ->
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    color = bulletColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = point,
                    color = SecondaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun LuckyItemCard(
    luckyItem: String,
    luckyColor: String
) {
    SurfaceCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(Res.string.dream_interpretation_lucky_item_title),
                color = PrimaryText,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LuckyInfoBox(
                    modifier = Modifier.weight(1f),
                    symbol = "◇",
                    title = stringResource(Res.string.dream_interpretation_lucky_object_title),
                    value = luckyItem
                )

                LuckyInfoBox(
                    modifier = Modifier.weight(1f),
                    symbol = "○",
                    title = stringResource(Res.string.dream_interpretation_lucky_color_title),
                    value = luckyColor
                )
            }
        }
    }
}

@Composable
private fun LuckyInfoBox(
    modifier: Modifier = Modifier,
    symbol: String,
    title: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFAFBFD))
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = symbol,
            color = Purple,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            color = SecondaryText,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = PrimaryText,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EnergyBar(energyPercent: Int) {
    val clamped = energyPercent.coerceIn(0, 100)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F2F6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped / 100f)
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Pink, Color(0xFFC8A5FF), Purple)
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .padding(start = ((clamped / 100f) * 260).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(BorderStroke(2.dp, Purple), CircleShape)
        )
    }
}

@Composable
private fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = PrimaryText
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

private fun String.formatAnalysis(): String {
    return split(". ")
        .filter { it.isNotBlank() }
        .joinToString(separator = ".\n\n") { part ->
            if (part.endsWith(".")) part else "$part."
        }
        .replace("..", ".")
}

private fun buildStars(score: Int): String {
    val filled = when (score.coerceIn(0, 100)) {
        in 0..19 -> 1
        in 20..39 -> 2
        in 40..59 -> 3
        in 60..79 -> 4
        else -> 5
    }

    return buildString {
        repeat(filled) { append("★") }
        repeat(5 - filled) { append("☆") }
    }
}
