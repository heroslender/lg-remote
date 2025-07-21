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
                it.copy(
                    device = device,
                )
            }

            if (device == null) {
                _uiState.tryEmit(HomeUiState())
                return@launchCollect
            }

            device.displayName.launchCollect { displayName ->
                _uiState.update {
                    it.copy(
                        deviceName = if (displayName.isNullOrEmpty()) device.friendlyName else displayName,
                    )
                }
            }

            device.status.launchCollect { status ->
                _uiState.update {
                    it.copy(
                        deviceStatus = status,
                    )
                }
            }


            device.apps.launchCollect { apps ->
                _uiState.update {
                    it.copy(
                        apps = apps,
                    )
                }
            }

            device.inputs.launchCollect { inputs ->
                _uiState.update {
                    it.copy(
                        inputs = inputs,
                    )
                }
            }

            device.runningApp.launchCollect { runningApp ->
                _uiState.update {
                    it.copy(
                        runningApp = runningApp,
                    )
                }
            }

            settingsRepository.settingsFlow.launchCollect { settings ->
                _uiState.update {
                    it.copy(
                        isFavorite = device.id == settings.favoriteId,
                    )
                }
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

    private fun <T> Flow<T>.launchCollect(collector: FlowCollector<T>) {
        viewModelScope.launch {
            collect(collector)
        }
    }
}