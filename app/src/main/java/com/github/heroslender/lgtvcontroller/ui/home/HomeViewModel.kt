package com.github.heroslender.lgtvcontroller.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        deviceManager.connectedDevice.flatMapLatest { device ->
            if (device == null) {
                return@flatMapLatest flowOf(HomeUiState())
            }

            device.state.map { deviceState ->
                HomeUiState(
                    deviceID = device.id,
                    deviceName = if (deviceState.displayName.isNullOrEmpty()) device.friendlyName else deviceState.displayName,
                    deviceStatus = deviceState.status,
                    runningApp = deviceState.runningApp,
                    apps = deviceState.apps,
                    inputs = deviceState.inputs,
                    hasCapability = device::hasCapability,
                    executeButton = device::executeControllerButton,
                    launchApp = device::launchApp,
                    isKeyboardOpen = deviceState.isKeyboardOpen,
                    sendBackspace = device::sendDelete,
                    sendEnter = device::sendEnter,
                    sendText = device::sendText,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState())

    val errors: Flow<Snackbar> = deviceManager.connectedDevice.flatMapConcat { device ->
        if (device == null) {
            deviceManager.errors
        } else {
            merge(device.errors, deviceManager.errors)
        }
    }
}