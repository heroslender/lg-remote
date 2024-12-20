package com.github.heroslender.lgtvcontroller.device.impl

import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.NetworkDevice

class LgNetworkDevice(
    override val id: String,
    override val friendlyName: String,
    override val status: DeviceStatus,
    private val connectToDevice: () -> Unit
) : NetworkDevice {

    override fun connect() {
        connectToDevice()
    }
}
