package com.github.heroslender.lgtvcontroller.device.impl

import com.connectsdk.service.WebOSTVService
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LgDevice(
    val device: LgNetworkDevice,
    status: DeviceStatus = DeviceStatus.DISCONNECTED,
) : Device {
    private var _status: MutableStateFlow<DeviceStatus> =
        MutableStateFlow(status)
    override val status: StateFlow<DeviceStatus>
        get() = _status

    override val id: String
        get() = device.id

    override val friendlyName: String
        get() = device.friendlyName

    override val displayName: String?
        get() = device.displayName

    val service
        get() = device.device.getServiceByName(WebOSTVService.ID) as WebOSTVService

    override fun hasCapability(capability: String): Boolean {
        return device.device.hasCapability(capability)
    }

    override fun powerOff() {
        service.powerOff(null)
        disconnect()
    }

    override fun volumeUp() {
        service.volumeUp()
    }

    override fun volumeDown() {
        service.volumeDown()
    }

    override fun channelUp() {
        service.channelUp()
    }

    override fun channelDown() {
        service.channelDown()
    }

    override fun up() {
        service.keyControl.up(null)
    }

    override fun down() {
        service.keyControl.down(null)
    }

    override fun left() {
        service.keyControl.left(null)
    }

    override fun right() {
        service.keyControl.right(null)
    }

    override fun ok() {
        service.keyControl.ok(null)
    }

    override fun back() {
        service.keyControl.back(null)
    }

    override fun home() {
        service.keyControl.home(null)
    }

    override fun info() {
        service.sendSpecialKey("INFO")
    }

    override fun source() {
        service.getExternalInputList(null)
    }

    override fun menu() {
        service.sendSpecialKey("MENU")
    }

    override fun qmenu() {
        service.sendSpecialKey("QMENU")
    }

    override fun launchNetflix() = launchApp("netflix")

    override fun launchApp(appId: String) {
        service.launchApp(appId, null)
    }

    override fun disconnect() {
        device.device.disconnect()
    }

    override fun updateStatus(status: DeviceStatus) {
        _status.value = status
    }
}