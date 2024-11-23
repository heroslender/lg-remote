package com.github.heroslender.lgtvcontroller.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectsdk.device.ConnectableDevice
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<ControllerUiState> = deviceManager.connectedDevice.flatMapLatest { device ->
        if (device == null) {
            return@flatMapLatest flowOf(ControllerUiState())
        }

        combine(
            device.status,
            settingsRepository.settingsFlow
        ) { deviceStatus, settings ->
            ControllerUiState(
                device = device,
                deviceName = device.friendlyName,
                deviceStatus = deviceStatus,
                isFavorite = device.id == settings.favoriteId
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ControllerUiState())

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFavoriteId(
                if (isFavorite) deviceManager.connectedDevice.value?.id ?: "" else ""
            )
        }
    }

    fun connect(connectableDevice: ConnectableDevice) {
        deviceManager.connect(connectableDevice)
    }
}