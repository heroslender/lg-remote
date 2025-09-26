package com.github.heroslender.lgtvcontroller.ui.devicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
) : ViewModel() {
    val uiState: StateFlow<DeviceListUiState> = deviceManager.devices.map { devices ->
        DeviceListUiState(
            devices = devices.map { device ->
                DeviceItemData(
                    displayName = if (device.displayName.isNullOrEmpty()) device.friendlyName else device.displayName!!,
                    status = device.status,
                    isPoweredOn = true,
                    connect = device::connect
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DeviceListUiState())

    val errors: Flow<Snackbar> = deviceManager.connectedDevice.flatMapConcat { device ->
        if (device == null) {
            deviceManager.errors
        } else {
            merge(device.errors, deviceManager.errors)
        }
    }

    var deviceConnectedListener: () -> Unit = {}

    init {
        viewModelScope.launch {
            deviceManager.connectedDevice.stateIn(viewModelScope).collect {
                if (it != null) {
                    deviceConnectedListener()
                }
            }
        }
    }

    fun onDeviceConnected(onDeviceConnected: () -> Unit = {}) {
        this.deviceConnectedListener = onDeviceConnected
    }
}