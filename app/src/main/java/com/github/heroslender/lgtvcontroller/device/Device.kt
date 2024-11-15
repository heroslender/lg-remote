package com.github.heroslender.lgtvcontroller.device

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.ConnectableDeviceListener
import com.connectsdk.service.DeviceService
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.RemoteUtil

class Device(val device: ConnectableDevice) : ConnectableDeviceListener {
    private var _status: MutableState<DeviceStatus> = mutableStateOf(DeviceStatus.DISCONNECTED)
    val status: State<DeviceStatus>
        get() = _status

    val service
        get() = device.getServiceByName(WebOSTVService.ID) as WebOSTVService

    fun powerOff() {
        service.powerOff(null)
        disconnect()
    }

    fun disconnect() {
        device.removeListener(this)
        _status.value = DeviceStatus.DISCONNECTED
        device.disconnect()
        RemoteUtil.instance.setDeviceDisconnected()
    }

    override fun onDeviceReady(device: ConnectableDevice) {
        _status.value = DeviceStatus.CONNECTED
        Log.d("Device", "listDevices :::: onDeviceReady")
    }

    override fun onDeviceDisconnected(device: ConnectableDevice) {
        Log.d("Device", "listDevices :::: onDeviceDisconnected")
        disconnect()
    }

    override fun onPairingRequired(
        device: ConnectableDevice,
        service: DeviceService,
        pairingType: DeviceService.PairingType
    ) {
        Log.d("Device", "listDevices :::: onPairingRequired")
        _status.value = DeviceStatus.PAIRING
    }

    override fun onCapabilityUpdated(
        device: ConnectableDevice,
        added: MutableList<String>,
        removed: MutableList<String>
    ) {
        Log.d("Device", "listDevices :::: onCapabilityUpdated")
    }

    override fun onConnectionFailed(
        device: ConnectableDevice,
        error: ServiceCommandError
    ) {
        Log.d("Device", "listDevices :::: onConnectionFailed")
    }
}