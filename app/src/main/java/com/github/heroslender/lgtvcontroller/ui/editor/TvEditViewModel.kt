package com.github.heroslender.lgtvcontroller.ui.editor

import android.util.Log
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
import com.github.heroslender.lgtvcontroller.ui.preferences.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvEditViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val tvRepository: TvRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var tvUiState by mutableStateOf(TvUiState(TvDetails("Loading...", "Loading..."), this::validateDisplayName))
        private set

    init {
        viewModelScope.launch {
            val id: String = checkNotNull(savedStateHandle[NavDest.EditDevice.tvIdArg])
            val tv = tvRepository.getTvStream(id).first()
            val details = TvDetails(
                tvId = tv.id,
                tvName = tv.name,
                tvDisplayName = tv.displayName ?: tv.name,
                autoConnect = tv.autoConnect
            )
            tvUiState = tvUiState.copy(tvDetails = details, isValid = validateInput(details))
        }
    }

    fun updateUiState(uiState: TvDetails) {
        val oldState = tvUiState
        tvUiState = tvUiState.copy(tvDetails = uiState, isValid =  validateInput(uiState))

        if (tvUiState.isValid && oldState != tvUiState) {
            Log.d("TvEditViewModel", "Updating TV details: $tvUiState")
            save()
        }
    }

    fun validateInput(uiState: TvDetails = tvUiState.tvDetails): Boolean {
        return uiState.tvId != "Loading..."
    }

    fun validateDisplayName(displayName: String): String? {
        return if (displayName.isBlank()) {
            "Display name cannot be blank"
        } else {
            null
        }
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
    val tvDetails: TvDetails = TvDetails(),
    val displayNameValidator: InputValidator,
    val isValid: Boolean = false,
)

data class TvDetails(
    val tvId: String = "",
    val tvName: String = "",
    val tvDisplayName: String = "",
    val autoConnect: Boolean = false,
)

fun TvDetails.toTv(): Tv = Tv(
    id = tvId,
    name = tvName,
    displayName = tvDisplayName,
    autoConnect = autoConnect
)