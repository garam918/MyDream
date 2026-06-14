package com.garam.mydream.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.mydream.core.data.model.DreamReport
import com.garam.mydream.core.data.model.ReportType
import com.garam.mydream.core.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportUiState(
    val selectedType: ReportType = ReportType.WEEKLY,
    val report: DreamReport? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

class ReportViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    fun selectType(type: ReportType) {
        if (_uiState.value.selectedType == type && _uiState.value.report != null) return
        _uiState.update { it.copy(selectedType = type, report = null) }
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        val type = _uiState.value.selectedType
        _uiState.update { it.copy(isLoading = true, hasError = false) }

        runCatching { repository.getDreamReport(type) }
            .onSuccess { report ->
                if (_uiState.value.selectedType == type) {
                    _uiState.update {
                        it.copy(report = report, isLoading = false, hasError = report == null)
                    }
                }
            }
            .onFailure {
                if (_uiState.value.selectedType == type) {
                    _uiState.update { it.copy(isLoading = false, hasError = true) }
                }
            }
    }
}
