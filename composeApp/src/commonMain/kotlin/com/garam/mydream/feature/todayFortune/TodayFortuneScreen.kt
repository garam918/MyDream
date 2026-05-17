package com.garam.mydream.feature.todayFortune

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.garam.mydream.core.database.LuckyItemRecommendation
import com.garam.mydream.core.database.TodayFortuneEntity
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import com.garam.mydream.core.util.localDateToDateText
import kotlinx.datetime.LocalDate
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.today_fortune_empty_items
import mydream.composeapp.generated.resources.today_fortune_error_default
import mydream.composeapp.generated.resources.today_fortune_lucky_color
import mydream.composeapp.generated.resources.today_fortune_lucky_item
import mydream.composeapp.generated.resources.today_fortune_lucky_number
import mydream.composeapp.generated.resources.today_fortune_item_section
import mydream.composeapp.generated.resources.today_fortune_loading
import mydream.composeapp.generated.resources.today_fortune_retry
import mydream.composeapp.generated.resources.today_fortune_subtitle
import mydream.composeapp.generated.resources.today_fortune_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TodayFortuneScreen(
    viewModel: TodayFortuneViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTheme.colors.mainBackgroundColor)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MyTheme.colors.dreamBtnColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.today_fortune_loading),
                        color = MyTheme.colors.textWhiteColor,
                        fontFamily = fontFamily()
                    )
                }
            }

            uiState.fortune != null -> {
                TodayFortuneContent(
                    fortune = uiState.fortune!!
                )
            }

            else -> {
                FortuneErrorContent(
                    message = uiState.errorMessage ?: stringResource(Res.string.today_fortune_error_default),
                    onRetry = viewModel::loadTodayFortune
                )
            }
        }
    }
}

@Composable
private fun TodayFortuneContent(
    fortune: TodayFortuneEntity
) {
    val fortuneDate = LocalDate.parse(fortune.fortuneDate)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.today_fortune_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.today_fortune_subtitle, localDateToDateText(fortuneDate)),
                style = MaterialTheme.typography.bodyMedium,
                color = MyTheme.colors.textWhiteColor.copy(alpha = 0.7f),
                fontFamily = fontFamily()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MyTheme.colors.cardBgColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = fortune.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MyTheme.colors.dreamBtnColor,
                        fontFamily = fontFamily()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = fortune.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MyTheme.colors.textWhiteColor.copy(alpha = 0.88f),
                        fontFamily = fontFamily()
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.today_fortune_item_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (fortune.luckyItems.isEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.today_fortune_empty_items),
                    color = MyTheme.colors.textWhiteColor.copy(alpha = 0.7f),
                    fontFamily = fontFamily()
                )
            }
        } else {
            items(fortune.luckyItems.size) { index ->
                LuckyItemCard(
                    item = fortune.luckyItems[index]
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LuckyItemCard(
    item: LuckyItemRecommendation
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MyTheme.colors.luckyItemCardBgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = luckyItemTitle(item),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MyTheme.colors.dreamBtnColor,
                fontFamily = fontFamily()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MyTheme.colors.textWhiteColor.copy(alpha = 0.72f),
                fontFamily = fontFamily()
            )
        }
    }
}

@Composable
private fun luckyItemTitle(item: LuckyItemRecommendation): String {
    return when (item.type) {
        "number" -> stringResource(Res.string.today_fortune_lucky_number)
        "item" -> stringResource(Res.string.today_fortune_lucky_item)
        "color" -> stringResource(Res.string.today_fortune_lucky_color)
        else -> item.name
    }
}

@Composable
private fun FortuneErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MyTheme.colors.textWhiteColor,
            fontFamily = fontFamily()
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MyTheme.colors.dreamBtnColor
            )
        ) {
            Text(
                text = stringResource(Res.string.today_fortune_retry),
                color = MyTheme.colors.btnTextColor,
                fontFamily = fontFamily()
            )
        }
    }
}
