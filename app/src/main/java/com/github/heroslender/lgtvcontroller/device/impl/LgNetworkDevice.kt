package com.github.heroslender.lgtvcontroller.device.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.NetworkDevice

class LgNetworkDevice(
    override val id: String,
    override val friendlyName: String,
    status: DeviceStatus,
    private val connectToDevice: () -> Unit
) : NetworkDevice {
    private val _status: MutableState<DeviceStatus> = mutableStateOf(status)
    override val status: State<DeviceStatus>
        get() = _status

    override fun connect() {
        connectToDevice()
    }

    override fun updateStatus(status: DeviceStatus) {
        _status.value = status
    }
}
