package com.github.heroslender.lgtvcontroller.ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.storage.TvRepository
import com.github.heroslender.lgtvcontroller.ui.navigation.NavDest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvEditViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val tvRepository: TvRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var tvUiState by mutableStateOf(TvUiState(TvDetails("Loading...", "Loading...")))
        private set

    init {
        viewModelScope.launch {
            val id: String = checkNotNull(savedStateHandle[NavDest.EditDevice.tvIdArg])

            tvRepository.getTvStream(id).collect {
                updateUiState(TvDetails(it.id, it.name, it.displayName ?: ""))
            }
        }
    }

    fun updateUiState(uiState: TvDetails) {
        tvUiState = TvUiState(uiState, validateInput(uiState))
    }

    fun validateInput(uiState: TvDetails = tvUiState.tvDetails): Boolean {
        return uiState.tvDisplayName.isNotEmpty() && uiState.tvId != "Loading..."
    }

    fun save() {
        if (validateInput()) {
            val tv = tvUiState.tvDetails
                // Remove trailing whitespaces added sometimes with autocomplete
                .run { copy(tvDisplayName = tvDisplayName.trim()) }
                .toTv()

            deviceManager.updateTv(tv)
        }
    }
}

data class TvUiState(
    var tvDetails: TvDetails = TvDetails(),
    var isValid: Boolean = false,
)

data class TvDetails(
    val tvId: String = "",
    val tvName: String = "",
    var tvDisplayName: String = "",
)

fun TvDetails.toTv(): Tv = Tv(tvId, tvName, tvDisplayName)