package com.github.heroslender.lgtvcontroller.device.impl

import com.connectsdk.device.ConnectableDevice
import com.connectsdk.service.WebOSTVService
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LgDevice(val device: ConnectableDevice) : Device {
    private var _status: MutableStateFlow<DeviceStatus> =
        MutableStateFlow(DeviceStatus.DISCONNECTED)
    override val status: StateFlow<DeviceStatus>
        get() = _status

    override val id: String
        get() = device.id

    override val friendlyName: String
        get() = device.friendlyName

    val service
        get() = device.getServiceByName(WebOSTVService.ID) as WebOSTVService

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

    override fun launchNetflix() = launchApp("netflix")

    override fun launchApp(appId: String) {
        service.launchApp(appId, null)
    }

    override fun disconnect() {
        device.disconnect()
    }

    override fun updateStatus(status: DeviceStatus) {
        _status.value = status
    }
}