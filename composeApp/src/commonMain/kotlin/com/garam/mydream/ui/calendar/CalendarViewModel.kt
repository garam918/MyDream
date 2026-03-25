package com.garam.mydream.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.data.local.DreamAnalysisEntity
import com.garam.mydream.data.repository.MainRepository
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

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

    fun getDreams() = viewModelScope.launch {
        repository.getDreamAnalysis().collect {

            _dreamContentList.value = it

            println("dreamList $it")

        }

    }

}