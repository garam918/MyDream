package com.garam.mydream.feature.dreamInterpretation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import mydream.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class DreamResultColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val nestedCardBackground: Color,
    val cardBorder: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val purple: Color,
    val purpleSoft: Color,
    val pink: Color,
    val gold: Color,
    val blueSoft: Color,
    val blueBorder: Color,
    val redSoft: Color,
    val redBorder: Color,
    val energyTrack: Color,
    val badgeBackground: Color
)

@Composable
private fun dreamResultColors(): DreamResultColors {
    val isDark = MyTheme.colors.mainBackgroundColor == Color(0xFF0B0E20)

    return if (isDark) {
        DreamResultColors(
            screenBackground = MyTheme.colors.mainBackgroundColor,
            cardBackground = Color(0xFF171C31),
            nestedCardBackground = Color(0xFF202744),
            cardBorder = Color(0xFF2B3455),
            primaryText = MyTheme.colors.textWhiteColor,
            secondaryText = Color(0xFFB8C1D9),
            purple = Color(0xFFC8A5FF),
            purpleSoft = Color(0xFF2B2144),
            pink = Color(0xFFFF8FAA),
            gold = MyTheme.colors.secondaryColor,
            blueSoft = Color(0xFF142747),
            blueBorder = Color(0xFF31588E),
            redSoft = Color(0xFF3D2029),
            redBorder = Color(0xFF7B3A44),
            energyTrack = Color(0xFF29314E),
            badgeBackground = Color(0xFF11162A)
        )
    } else {
        DreamResultColors(
            screenBackground = Color(0xFFFDF9F4),
            cardBackground = Color.White,
            nestedCardBackground = Color(0xFFFAFBFD),
            cardBorder = Color(0xFFE5E7EB),
            primaryText = Color(0xFF151A2D),
            secondaryText = Color(0xFF6D748B),
            purple = Color(0xFF6E54A3),
            purpleSoft = Color(0xFFF2EBFF),
            pink = Color(0xFFF7A3AF),
            gold = Color(0xFFF0C261),
            blueSoft = Color(0xFFF4F8FF),
            blueBorder = Color(0xFFD3E3FF),
            redSoft = Color(0xFFFFF6F5),
            redBorder = Color(0xFFFFD9D4),
            energyTrack = Color(0xFFF0F2F6),
            badgeBackground = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamInterpretation(
    dreamResponse: DreamResponse,
    onBackPressed: () -> Unit
) {
    val colors = dreamResultColors()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.dream_interpretation_screen_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily(),
                        color = colors.primaryText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(Res.drawable.back_btn_icon),
                            contentDescription = stringResource(Res.string.common_back),
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = colors.screenBackground,
                    titleContentColor = colors.primaryText,
                    navigationIconContentColor = colors.primaryText,
                    actionIconContentColor = colors.primaryText,
                    scrolledContainerColor = colors.screenBackground,
                    subtitleContentColor = colors.primaryText
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = colors.screenBackground,
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.screenBackground)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.9f))
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
    val colors = dreamResultColors()

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
                        .background(colors.purpleSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI",
                        color = colors.purple,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = stringResource(Res.string.dream_interpretation_analysis_title),
                    color = colors.primaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = dreamResponse.analysis.formatAnalysis(),
                color = colors.secondaryText,
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
                    color = colors.secondaryText,
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
                    color = colors.purple,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(Res.string.dream_interpretation_energy_positive),
                    color = colors.secondaryText,
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
    val colors = dreamResultColors()

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
                    color = colors.secondaryText,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${score.coerceIn(0, 100)}",
                        color = colors.primaryText,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        lineHeight = 48.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(Res.string.dream_interpretation_score_unit),
                        color = colors.gold,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 7.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = buildStars(score),
                    color = colors.gold,
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
    val colors = dreamResultColors()

    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(colors.badgeBackground),
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
                            colors.gold.copy(alpha = 0.15f),
                            colors.gold,
                            colors.gold.copy(alpha = 0.15f),
                            colors.gold
                        )
                    ),
                    shape = CircleShape
                )
        )

        Text(
            text = "✦",
            color = colors.gold,
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
    val colors = dreamResultColors()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        InsightCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = stringResource(Res.string.dream_interpretation_expect_title),
            background = colors.blueSoft,
            border = colors.blueBorder,
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
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = stringResource(Res.string.dream_interpretation_caution_title),
            background = colors.redSoft,
            border = colors.redBorder,
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
    val colors = dreamResultColors()

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
                color = colors.primaryText,
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
                    color = colors.secondaryText,
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
    val colors = dreamResultColors()

    SurfaceCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(Res.string.dream_interpretation_lucky_item_title),
                color = colors.primaryText,
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
    val colors = dreamResultColors()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.nestedCardBackground)
            .border(BorderStroke(1.dp, colors.cardBorder), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = symbol,
            color = colors.purple,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            color = colors.secondaryText,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = colors.primaryText,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EnergyBar(energyPercent: Int) {
    val colors = dreamResultColors()
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
                .background(colors.energyTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped / 100f)
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.pink, Color(0xFFC8A5FF), colors.purple)
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .padding(start = ((clamped / 100f) * 260).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(colors.cardBackground)
                .border(BorderStroke(2.dp, colors.purple), CircleShape)
        )
    }
}

@Composable
private fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = dreamResultColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, colors.cardBorder),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground,
            contentColor = colors.primaryText
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
