package com.github.heroslender.lgtvcontroller.devicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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
                    displayName = device.friendlyName,
                    status = device.status,
                    isPoweredOn = true,
                    connect = {
                        val curr = deviceManager.connectedDevice.value
                        if (curr != null) {
                            if (curr.id == device.id) {
                                // Already connected to this
                                return@DeviceItemData
                            }

                            curr.disconnect()
                        }

                        device.connect()
                    }
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DeviceListUiState())

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