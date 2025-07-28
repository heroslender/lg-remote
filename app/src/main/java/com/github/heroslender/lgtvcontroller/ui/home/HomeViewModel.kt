package com.github.heroslender.lgtvcontroller.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        deviceManager.connectedDevice.launchCollect { device ->
            _uiState.update {
                it.copy(device = device,)
            }

            if (device == null) {
                _uiState.tryEmit(HomeUiState())
                return@launchCollect
            }

            device.displayName.bindToState(_uiState) { state, displayName ->
                state.copy(deviceName = if (displayName.isNullOrEmpty()) device.friendlyName else displayName)
            }

            device.status.bindToState(_uiState) { state, status ->
                state.copy(deviceStatus = status)
            }


            device.apps.bindToState(_uiState) { state, apps ->
                state.copy(apps = apps)
            }

            device.inputs.bindToState(_uiState) { state, inputs ->
                state.copy(inputs = inputs)
            }

            device.runningApp.bindToState(_uiState) { state, runningApp ->
                state.copy(runningApp = runningApp)
            }

            settingsRepository.settingsFlow.bindToState(_uiState) { state, settings ->
                state.copy(isFavorite = device.id == settings.favoriteId)
            }
        }
    }

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFavoriteId(
                if (isFavorite) deviceManager.connectedDevice.value?.id ?: "" else ""
            )
        }
    }

    private fun <T, S> Flow<T>.bindToState(state: MutableStateFlow<S>, mapper: (S, T) -> S) {
        launchCollect { value ->
            state.update { state ->
                mapper(state, value)
            }
        }
    }

    private fun <T> Flow<T>.launchCollect(collector: FlowCollector<T>) {
        viewModelScope.launch {
            collect(collector)
        }
    }
}