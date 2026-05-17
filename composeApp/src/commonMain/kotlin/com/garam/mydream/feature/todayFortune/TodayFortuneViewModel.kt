package com.garam.mydream.feature.todayFortune

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.core.data.repository.MainRepository
import com.garam.mydream.core.database.LuckyItemRecommendation
import com.garam.mydream.core.database.TodayFortuneEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class TodayFortuneUiState(
    val isLoading: Boolean = true,
    val fortune: TodayFortuneEntity? = null,
    val errorMessage: String? = null
)

class TodayFortuneViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayFortuneUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTodayFortune()
    }

    fun loadTodayFortune() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val cachedFortune = repository.getTodayFortune(today)
            if (cachedFortune != null) {
                _uiState.value = TodayFortuneUiState(
                    isLoading = false,
                    fortune = cachedFortune
                )
                return@launch
            }

            val result = repository.fetchTodayFortune(today)
            val response = result.getOrNull()

            if (response != null) {
                val fortune = TodayFortuneEntity(
                    uid = "",
                    fortuneDate = today,
                    title = response.label,
                    summary = response.content,
                    score = 0,
                    luckyItems = listOf(
                        LuckyItemRecommendation(
                            type = "number",
                            name = response.lucky.number.value,
                            description = response.lucky.number.reason,
                            partnerUrl = ""
                        ),
                        LuckyItemRecommendation(
                            type = "item",
                            name = response.lucky.item.name,
                            description = response.lucky.item.reason,
                            partnerUrl = ""
                        ),
                        LuckyItemRecommendation(
                            type = "color",
                            name = response.lucky.color.name,
                            description = response.lucky.color.reason,
                            partnerUrl = ""
                        )
                    ),
                    generatedAt = Clock.System.now().toString()
                )
                repository.saveTodayFortune(fortune)
                _uiState.value = TodayFortuneUiState(
                    isLoading = false,
                    fortune = fortune
                )
            } else {
                _uiState.value = TodayFortuneUiState(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
