package com.garam.mydream.ui.record

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.composable.RewardedAd
import com.garam.mydream.data.remote.DreamResponse
import com.garam.mydream.resources.MyTheme
import com.garam.mydream.resources.fontFamily
import com.garam.mydream.ui.ads.AdScreen
import com.garam.mydream.ui.dialog.DateSelectDialog
import com.garam.mydream.util.localDateToText
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.common_cancel
import mydream.composeapp.generated.resources.common_confirm
import mydream.composeapp.generated.resources.dream_record_analysis_btn_text
import mydream.composeapp.generated.resources.dream_record_blank_content_message
import mydream.composeapp.generated.resources.dream_record_dream_content_place_holder_text
import mydream.composeapp.generated.resources.dream_record_dream_content_title_text
import mydream.composeapp.generated.resources.dream_record_dream_label_place_holder_text
import mydream.composeapp.generated.resources.dream_record_dream_label_text
import mydream.composeapp.generated.resources.dream_record_explain_text
import mydream.composeapp.generated.resources.dream_record_limit_ad_available
import mydream.composeapp.generated.resources.dream_record_limit_exhausted
import mydream.composeapp.generated.resources.dream_record_limit_loading
import mydream.composeapp.generated.resources.dream_record_limit_remaining
import mydream.composeapp.generated.resources.dream_record_reward_ad_description
import mydream.composeapp.generated.resources.dream_record_reward_ad_failed
import mydream.composeapp.generated.resources.dream_record_reward_ad_loading
import mydream.composeapp.generated.resources.dream_record_reward_ad_not_completed
import mydream.composeapp.generated.resources.dream_record_reward_ad_success
import mydream.composeapp.generated.resources.dream_record_reward_ad_title
import mydream.composeapp.generated.resources.dream_record_submit_failed
import mydream.composeapp.generated.resources.dream_record_title_text
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, DependsOnGoogleMobileAds::class)
@Composable
fun DreamRecord(
    onNavigateToDreamInterpretation: (DreamResponse) -> Unit,
    viewModel: RecordViewModel = koinViewModel()
) {
    var dreamTitle by remember { mutableStateOf("") }
    var dreamContent by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isCalendarDialogShow by remember { mutableStateOf(false) }
    var dreamAnalysisEnabled by remember { mutableStateOf(true) }
    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var showRewardedAd by remember { mutableStateOf(false) }
    var rewardGranted by remember { mutableStateOf(false) }
    var shouldRunInterpretationAfterReward by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val limitState by viewModel.limitState.collectAsState()
    val blankContentMessage = stringResource(Res.string.dream_record_blank_content_message)
    val submitFailedMessage = stringResource(Res.string.dream_record_submit_failed)
    val rewardAdLoadingMessage = stringResource(Res.string.dream_record_reward_ad_loading)
    val rewardAdSuccessMessage = stringResource(Res.string.dream_record_reward_ad_success)
    val rewardAdNotCompletedMessage = stringResource(Res.string.dream_record_reward_ad_not_completed)
    val rewardAdFailedMessage = stringResource(Res.string.dream_record_reward_ad_failed)
    val limitExhaustedMessage = stringResource(Res.string.dream_record_limit_exhausted)

    @Composable
    fun limitGuideText(): String {
        return when {
            limitState.isLoading -> stringResource(Res.string.dream_record_limit_loading)
            limitState.canInterpret -> stringResource(
                Res.string.dream_record_limit_remaining,
                limitState.remainingCount
            )
            limitState.canWatchRewardedAd -> stringResource(Res.string.dream_record_limit_ad_available)
            else -> stringResource(Res.string.dream_record_limit_exhausted)
        }
    }

    fun updateStatus(message: String?) {
        statusMessage = message
    }

    fun runDreamInterpretation() {
        if (dreamContent.isBlank()) {
            updateStatus(blankContentMessage)
            return
        }

        dreamAnalysisEnabled = false
        updateStatus(null)

        scope.launch {
            val result = viewModel.analyzeDream(dreamContent).await()
            val dreamAnalysis = result.getOrNull()

            if (dreamAnalysis != null) {
                viewModel.savedDreamAnalysis(dreamContent = dreamAnalysis, date = selectedDate.toString())
                viewModel.consumeInterpretationChance()
                onNavigateToDreamInterpretation(dreamAnalysis)
            } else {
                updateStatus(
                    result.exceptionOrNull()?.message ?: submitFailedMessage
                )
                viewModel.refreshLimitState()
            }

            dreamAnalysisEnabled = true
        }
    }

    if (isCalendarDialogShow) {
        DateSelectDialog(
            selectedDate = selectedDate,
            onDismiss = {
                isCalendarDialogShow = false
            },
            onDateSelected = { date ->
                selectedDate = date
            }
        )
    }

    if (showRewardedAdDialog) {
        AlertDialog(
            onDismissRequest = {
                showRewardedAdDialog = false
            },
            title = {
                Text(
                    text = stringResource(Res.string.dream_record_reward_ad_title),
                    color = MyTheme.colors.textWhiteColor,
                    fontFamily = fontFamily()
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.dream_record_reward_ad_description),
                    color = MyTheme.colors.textWhiteColor.copy(alpha = 0.8f),
                    fontFamily = fontFamily()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        rewardGranted = false
                        shouldRunInterpretationAfterReward = false
                        showRewardedAdDialog = false
                        showRewardedAd = true
                        updateStatus(rewardAdLoadingMessage)
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.common_confirm),
                        color = MyTheme.colors.dreamBtnColor
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRewardedAdDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.common_cancel),
                        color = MyTheme.colors.textWhiteColor.copy(alpha = 0.7f)
                    )
                }
            },
            containerColor = MyTheme.colors.cardBgColor
        )
    }

    if (showRewardedAd) {
        RewardedAd(
            onRewardEarned = {
                rewardGranted = true
                shouldRunInterpretationAfterReward = true
                updateStatus(rewardAdSuccessMessage)
                scope.launch {
                    viewModel.grantRewardedInterpretationChance()
                }
            },
            onDismissed = {
                showRewardedAd = false
                if (rewardGranted && shouldRunInterpretationAfterReward) {
                    shouldRunInterpretationAfterReward = false
                    runDreamInterpretation()
                } else {
                    updateStatus(rewardAdNotCompletedMessage)
                }
            },
            onFailure = {
                showRewardedAd = false
                rewardGranted = false
                shouldRunInterpretationAfterReward = false
                updateStatus(rewardAdFailedMessage)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MyTheme.colors.mainBackgroundColor)
            .padding(horizontal = 14.dp)
    ) {

        Column {
            TextButton(
                onClick = {
                    isCalendarDialogShow = true
                },
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(width = 1.dp, color = Color.LightGray)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = localDateToText(selectedDate),
                    color = MyTheme.colors.textWhiteColor,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(Res.string.dream_record_title_text),
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.dream_record_explain_text),
                color = MyTheme.colors.textWhiteColor.copy(alpha = 0.5f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }

        Column {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(Res.string.dream_record_dream_label_text),
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            TextField(
                value = dreamTitle,
                onValueChange = {
                    dreamTitle = it
                },
                placeholder = {
                    Text(text = stringResource(Res.string.dream_record_dream_label_place_holder_text))
                },
                minLines = 1,
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MyTheme.colors.cardBgColor,
                    focusedContainerColor = MyTheme.colors.cardBgColor,
                    unfocusedContainerColor = MyTheme.colors.cardBgColor,
                    focusedTextColor = MyTheme.colors.textWhiteColor,
                    disabledTextColor = MyTheme.colors.textWhiteColor,
                    unfocusedTextColor = MyTheme.colors.textWhiteColor,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(Res.string.dream_record_dream_content_title_text),
                color = MyTheme.colors.textWhiteColor,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            TextField(
                value = dreamContent,
                onValueChange = {
                    dreamContent = it
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(text = stringResource(Res.string.dream_record_dream_content_place_holder_text))
                },
                minLines = 6,
                maxLines = 6,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MyTheme.colors.cardBgColor,
                    focusedContainerColor = MyTheme.colors.cardBgColor,
                    unfocusedContainerColor = MyTheme.colors.cardBgColor,
                    focusedTextColor = MyTheme.colors.textWhiteColor,
                    disabledTextColor = MyTheme.colors.textWhiteColor,
                    unfocusedTextColor = MyTheme.colors.textWhiteColor,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyRow {
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = limitGuideText(),
            color = MyTheme.colors.textWhiteColor.copy(alpha = 0.75f),
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )

        statusMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MyTheme.colors.textWhiteColor.copy(alpha = 0.9f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            enabled = dreamAnalysisEnabled && !limitState.isLoading,
            onClick = {
                when {
                    limitState.canInterpret -> runDreamInterpretation()
                    limitState.canWatchRewardedAd -> {
                        if (dreamContent.isBlank()) {
                            updateStatus(blankContentMessage)
                        } else {
                            showRewardedAdDialog = true
                        }
                    }
                    else -> {
                        updateStatus(limitExhaustedMessage)
                    }
                }
            },
            shape = RoundedCornerShape(36.dp),
            colors = ButtonColors(
                contentColor = MyTheme.colors.dreamBtnColor,
                containerColor = MyTheme.colors.dreamBtnColor,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.dream_record_analysis_btn_text),
                color = MyTheme.colors.btnTextColor
            )
        }

        AdScreen()
    }
}
