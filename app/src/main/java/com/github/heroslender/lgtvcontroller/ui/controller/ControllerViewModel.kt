package com.github.heroslender.lgtvcontroller.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<ControllerUiState> =
        deviceManager.connectedDevice.flatMapLatest { device ->
            if (device == null) {
                return@flatMapLatest flowOf(ControllerUiState())
            }

            combine(
                device.status,
                device.displayName,
                settingsRepository.settingsFlow,
            ) { deviceStatus, displayName, settings ->
                ControllerUiState(
                    device = device,
                    deviceName = if (displayName.isNullOrEmpty()) device.friendlyName else displayName,
                    deviceStatus = deviceStatus,
                    isFavorite = device.id == settings.favoriteId,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ControllerUiState())

    val errors: Flow<Snackbar> = deviceManager.connectedDevice.flatMapConcat { device ->
        if (device == null) {
            return@flatMapConcat emptyFlow()
        }

        device.errors
    }

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFavoriteId(
                if (isFavorite) deviceManager.connectedDevice.value?.id ?: "" else ""
            )
        }
    }
}