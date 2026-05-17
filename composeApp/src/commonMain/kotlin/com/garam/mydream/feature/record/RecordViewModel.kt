package com.garam.mydream.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.UserDataDao
import com.garam.mydream.core.database.UserDataEntity
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.data.repository.MainRepository
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class DreamInterpretationLimitState(
    val remainingCount: Int = 2,
    val rewardedChanceUsed: Boolean = false,
    val isLoading: Boolean = true
) {
    val canInterpret: Boolean
        get() = remainingCount > 0

    val canWatchRewardedAd: Boolean
        get() = remainingCount == 0 && !rewardedChanceUsed

    val isExhaustedForToday: Boolean
        get() = remainingCount == 0 && rewardedChanceUsed
}

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
class RecordViewModel(
    private val repository: MainRepository,
    private val userDataDao: UserDataDao,
    private val authRepository: AuthRepository,
    private val firebaseDataSource: FirebaseDataSource
) : ViewModel() {

    companion object {
        private const val DAILY_FREE_INTERPRETATION_COUNT = 2

        private fun today(): String {
            return Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        }
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _limitState = MutableStateFlow(DreamInterpretationLimitState())
    val limitState = _limitState.asStateFlow()

    init {
        refreshLimitState()
    }

    fun refreshLimitState() = viewModelScope.launch {
        val managedUser = getManagedUserData()
        _limitState.value = managedUser?.toLimitState() ?: DreamInterpretationLimitState(isLoading = false)
    }

    fun analyzeDream(dreamContent: String) = viewModelScope.async {
        repository.sendDreamContent(dreamContent)
    }

    fun savedDreamAnalysis(dreamContent: DreamResponse, date: String) = viewModelScope.launch {

        val dreamContentEntity = DreamAnalysisEntity(
            id = Uuid.random().toString(),
            uid = "",
            title = dreamContent.title,
            score = dreamContent.score,
            analysis = dreamContent.analysis,
            energy_label = dreamContent.energy_label,
            energy_percent = dreamContent.energy_percent,
            good_points = dreamContent.good_points,
            warn_points = dreamContent.warn_points,
            lucky_item = dreamContent.lucky_item,
            lucky_color = dreamContent.lucky_color,
            analysisDate = date
        )

        repository.saveDreamAnalysis(dreamContentEntity)
    }

    suspend fun consumeInterpretationChance(): Boolean {
        val user = getManagedUserData() ?: return false
        if (user.usageCount <= 0) {
            _limitState.value = user.toLimitState()
            return false
        }

        val updatedUser = user.copy(
            usageCount = (user.usageCount - 1).coerceAtLeast(0),
            lastUseDate = today()
        )
        persistUserData(updatedUser)
        return true
    }

    suspend fun grantRewardedInterpretationChance(): Boolean {
        val user = getManagedUserData() ?: return false
        if (user.usageCount > 0 || user.rewardedChanceUsed) {
            _limitState.value = user.toLimitState()
            return false
        }

        val updatedUser = user.copy(
            usageCount = 1,
            rewardedChanceUsed = true,
            lastUseDate = today()
        )
        persistUserData(updatedUser)
        return true
    }

    private suspend fun getManagedUserData(): UserDataEntity? {
        val currentUser = authRepository.currentUser() ?: return null
        val localUser = userDataDao.getUserData(currentUser.uid)
        val sourceUser = localUser ?: currentUser
        val normalizedUser = normalizeUserData(sourceUser)

        if (normalizedUser != localUser) {
            persistUserData(normalizedUser)
        } else {
            _limitState.value = normalizedUser.toLimitState()
        }

        return normalizedUser
    }

    private fun normalizeUserData(user: UserDataEntity): UserDataEntity {
        val today = today()
        return if (user.lastUseDate != today) {
            user.copy(
                usageCount = DAILY_FREE_INTERPRETATION_COUNT,
                rewardedChanceUsed = false,
                lastUseDate = today
            )
        } else {
            user.copy(
                usageCount = user.usageCount.coerceAtLeast(0),
                lastUseDate = today
            )
        }
    }

    private suspend fun persistUserData(user: UserDataEntity) {
        userDataDao.upsertUserData(user)
        runCatching {
            firebaseDataSource.setUserData(user)
        }
        _limitState.value = user.toLimitState()
    }

    private fun UserDataEntity.toLimitState(): DreamInterpretationLimitState {
        return DreamInterpretationLimitState(
            remainingCount = usageCount.coerceAtLeast(0),
            rewardedChanceUsed = rewardedChanceUsed,
            isLoading = false
        )
    }
}
