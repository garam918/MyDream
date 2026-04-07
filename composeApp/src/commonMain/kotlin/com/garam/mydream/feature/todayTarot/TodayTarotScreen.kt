package com.garam.mydream.feature.todayTarot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mydream.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class TarotCardModel(
    val id: Int,
    val name: String,
    val description: String
)

@Composable
fun getMajorArcanaDeck(): List<TarotCardModel> {
    return listOf(
        TarotCardModel(
            0,
            stringResource(Res.string.today_tarot_card_fool_name),
            stringResource(Res.string.today_tarot_card_fool_description)
        ),
        TarotCardModel(
            1,
            stringResource(Res.string.today_tarot_card_magician_name),
            stringResource(Res.string.today_tarot_card_magician_description)
        ),
        TarotCardModel(
            2,
            stringResource(Res.string.today_tarot_card_priestess_name),
            stringResource(Res.string.today_tarot_card_priestess_description)
        ),
        TarotCardModel(
            3,
            stringResource(Res.string.today_tarot_card_fool_name),
            stringResource(Res.string.today_tarot_card_fool_description)
        ),
        TarotCardModel(
            4,
            stringResource(Res.string.today_tarot_card_magician_name),
            stringResource(Res.string.today_tarot_card_magician_description)
        ),
        TarotCardModel(
            5,
            stringResource(Res.string.today_tarot_card_priestess_name),
            stringResource(Res.string.today_tarot_card_priestess_description)
        ),
        TarotCardModel(
            6,
            stringResource(Res.string.today_tarot_card_fool_name),
            stringResource(Res.string.today_tarot_card_fool_description)
        ),
        TarotCardModel(
            7,
            stringResource(Res.string.today_tarot_card_magician_name),
            stringResource(Res.string.today_tarot_card_magician_description)
        ),
        TarotCardModel(
            8,
            stringResource(Res.string.today_tarot_card_priestess_name),
            stringResource(Res.string.today_tarot_card_priestess_description)
        ),
        TarotCardModel(
            21,
            stringResource(Res.string.today_tarot_card_world_name),
            stringResource(Res.string.today_tarot_card_world_description)
        )
    )
}

@Composable
fun TodayTarotScreen(todayTarotViewModel: TodayTarotViewModel = koinViewModel()) {

    val deck = getMajorArcanaDeck()
    val dailyCards = remember(deck) {
        deck.shuffled().take(6)
    }

    // 2. 상태 관리
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showResultText by remember { mutableStateOf(false) }

    // 3. 텍스트 노출 타이밍 동기화
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != null) {
            delay(600) // 카드가 뒤집히는 애니메이션 시간(600ms) 대기
            showResultText = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 헤더 텍스트
        Text(
            text = stringResource(Res.string.today_tarot_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (selectedIndex == null) {
                stringResource(Res.string.today_tarot_subtitle_before_selection)
            } else {
                stringResource(Res.string.today_tarot_subtitle_after_selection)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // 3x2 그리드 레이아웃 (가로 3장, 세로 2장)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(dailyCards) { index, card ->
                // UX 디테일: 선택된 카드가 있으면, 선택되지 않은 나머지 카드들은 흐려짐(Dim 처리)
                val isSelected = selectedIndex == index
                val isAnyCardSelected = selectedIndex != null

                val alpha by animateFloatAsState(
                    targetValue = if (isAnyCardSelected && !isSelected) 0.3f else 1f,
                    animationSpec = tween(durationMillis = 500),
                    label = "cardAlpha"
                )

                // 이전에 작성했던 TarotCard 재사용
                TarotCard(
                    isFlipped = isSelected, // 선택된 카드만 뒤집힘
                    onClick = {
                        // 아무것도 선택되지 않았을 때만 터치 허용 (1회 뽑기 제한)
                        if (selectedIndex == null) {
                            selectedIndex = index
                        }
                    },
                    modifier = Modifier.alpha(alpha)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 결과 텍스트 영역
        AnimatedVisibility(
            visible = showResultText,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
        ) {
            selectedIndex?.let { index ->
                val selectedCard = dailyCards[index]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedCard.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = selectedCard.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f
                    )
                }
            }
        }
    }
}

@Composable
fun TarotCard(
    isFlipped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. 회전 각도 애니메이션 (0도 -> 180도)
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing // 쫀득한 애니메이션 효과
        ),
        label = "cardFlipAnimation"
    )

    Card(
        modifier = modifier
            .aspectRatio(0.6f) // 일반적인 타로 카드 비율 (약 3:5)
            .clickable { onClick() }
            .graphicsLayer {
                // [핵심 디테일 1] 3D 원근감 설정
                // 이 값이 없으면 카드가 화면에 딱 붙어서 2D로 찌그러지듯 돌아갑니다.
                // density를 곱해 기기 해상도에 맞게 원근감을 줍니다.
                cameraDistance = 12f * density

                // Y축을 기준으로 회전
                rotationY = rotation
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // 2. 각도(90도)를 기준으로 앞면/뒷면 스와핑
        if (rotation <= 90f) {
            // 카드가 90도 이하일 때는 타로 카드 뒷면(패턴)을 보여줌
            TarotCardBack(modifier = Modifier.fillMaxSize())
        } else {
            // [핵심 디테일 2] 좌우 반전 방지
            // 카드가 180도 회전한 상태이므로 안의 컨텐츠가 거울처럼 좌우 반전되어 보입니다.
            // 이를 막기 위해 컨텐츠 자체를 다시 180도 뒤집어줍니다.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                TarotCardFront() // 해몽 결과나 운세가 적힌 앞면
            }
        }
    }
}

@Composable
fun TarotCardBack(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        // 앱의 기본 톤앤매너에 맞는 보라색/우주 느낌의 카드 뒷면 이미지
        // Image(
        //     painter = painterResource(id = R.drawable.img_tarot_back),
        //     contentDescription = "타로 카드 뒷면",
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier.fillMaxSize()
        // )
        Text(text = "🌌", style = MaterialTheme.typography.displayLarge)
    }
}

@Composable
fun TarotCardFront(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // 해몽/타로 결과에 맞는 카드 앞면 일러스트
        // Image(
        //     painter = painterResource(id = R.drawable.img_tarot_hermit),
        //     contentDescription = "타로 카드 앞면",
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier.fillMaxSize()
        // )
        Text(text = "🧙‍♂️", style = MaterialTheme.typography.displayLarge)
    }
}
