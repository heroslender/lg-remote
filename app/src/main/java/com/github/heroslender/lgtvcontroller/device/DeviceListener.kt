package com.github.heroslender.lgtvcontroller.device

import android.util.Log
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.ConnectableDeviceListener
import com.connectsdk.service.DeviceService
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.DeviceManager

class DeviceListener(
    private val manager: DeviceManager,
    private val device: Device,
) : ConnectableDeviceListener {

    override fun onDeviceReady(d: ConnectableDevice) {
        device.updateStatus(DeviceStatus.CONNECTED)
        manager.onDeviceConnected(device)
        Log.d("Device", "listDevices :::: onDeviceReady")
    }

    override fun onDeviceDisconnected(d: ConnectableDevice) {
        Log.d("Device", "listDevices :::: onDeviceDisconnected")
        d.removeListener(this)
        manager.onDeviceDisconnected(device)
        manager.setDeviceDisconnected()
        device.updateStatus(DeviceStatus.DISCONNECTED)
    }

    override fun onPairingRequired(
        d: ConnectableDevice,
        service: DeviceService,
        pairingType: DeviceService.PairingType
    ) {
        Log.d("Device", "listDevices :::: onPairingRequired")
        manager.onDevicePairing(device)
        device.updateStatus(DeviceStatus.PAIRING)
    }

    override fun onCapabilityUpdated(
        d: ConnectableDevice,
        added: MutableList<String>,
        removed: MutableList<String>
    ) {
        Log.d("Device", "listDevices :::: onCapabilityUpdated")
    }

    override fun onConnectionFailed(
        d: ConnectableDevice,
        error: ServiceCommandError
    ) {
        Log.d("Device", "listDevices :::: onConnectionFailed")
    }
}