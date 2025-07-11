package com.github.heroslender.lgtvcontroller.device.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.connectsdk.device.ConnectableDevice
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.NetworkDevice
import com.github.heroslender.lgtvcontroller.domain.model.Tv

class LgNetworkDevice(
    val device: ConnectableDevice,
    override val tv: Tv,
    private val manager: DeviceManager,
) : NetworkDevice {
    override val id: String = device.id
    override val friendlyName: String = device.friendlyName

    private val _status: MutableState<DeviceStatus> =
        mutableStateOf(if (device.isConnected) DeviceStatus.CONNECTED else DeviceStatus.DISCONNECTED)
    override val status: State<DeviceStatus>
        get() = _status

    override fun connect() {
        manager.connect(this)
    }

    override fun updateStatus(status: DeviceStatus) {
        _status.value = status
    }
}
