package com.github.heroslender.lgtvcontroller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

open class BaseViewModel(
    private val deviceManager: DeviceManager,
) : ViewModel() {
    val tvTextInputState: StateFlow<TvTextInputState> =
        deviceManager.connectedDevice.flatMapLatest { device ->
            if (device == null) {
                return@flatMapLatest flowOf(TvTextInputState())
            }

            device.state.map { deviceState ->
                TvTextInputState(
                    isKeyboardOpen = deviceState.isKeyboardOpen,
                    sendBackspace = device::sendDelete,
                    sendEnter = device::sendEnter,
                    sendText = device::sendText,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, TvTextInputState())

    val errors: Flow<Snackbar> = deviceManager.connectedDevice.flatMapConcat { device ->
        if (device == null) {
            deviceManager.errors
        } else {
            merge(device.errors, deviceManager.errors)
        }
    }
}