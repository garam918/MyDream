package com.garam.mydream.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.data.repository.MainRepository
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class DreamDeleteState(
    val deletingDreamId: String? = null,
    val lastDeleteFailed: Boolean = false
)

class CalendarViewModel(
    private val repository: MainRepository
): ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    init {

        getDreams()

    }

    private val _dreamContentList = MutableStateFlow<List<DreamAnalysisEntity>>(listOf())
    val dreamContentList = _dreamContentList.asStateFlow()

    private val _dreamDeleteState = MutableStateFlow(DreamDeleteState())
    val dreamDeleteState = _dreamDeleteState.asStateFlow()

    fun getDreams() = viewModelScope.launch {
        repository.getDreamAnalysis().collect {

            _dreamContentList.value = it

            println("dreamList $it")

        }

    }

    fun deleteDream(dreamId: String) = viewModelScope.launch {
        _dreamDeleteState.value = DreamDeleteState(deletingDreamId = dreamId)

        runCatching {
            repository.deleteDreamAnalysis(dreamId)
        }.onSuccess {
            _dreamDeleteState.value = DreamDeleteState()
        }.onFailure {
            _dreamDeleteState.value = DreamDeleteState(lastDeleteFailed = true)
        }
    }

    fun clearDeleteFailure() {
        _dreamDeleteState.value = _dreamDeleteState.value.copy(lastDeleteFailed = false)
    }

}
