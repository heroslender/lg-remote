package com.github.heroslender.lgtvcontroller.ui.controller

import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    deviceManager: DeviceManager,
) : BaseViewModel(deviceManager) {
    val uiState: StateFlow<ControllerUiState> =
        deviceManager.connectedDevice.flatMapLatest { device ->
            if (device == null) {
                return@flatMapLatest flowOf(ControllerUiState())
            }

            device.state.map { deviceState ->
                ControllerUiState(
                    deviceName = if (deviceState.displayName.isNullOrEmpty()) device.friendlyName else deviceState.displayName,
                    deviceStatus = deviceState.status,
                    hasCapability = { device.hasCapability(it) },
                    executeButton = { device.executeControllerButton(it) },
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ControllerUiState())
}