package com.github.heroslender.lgtvcontroller

import android.content.Context
import android.util.Log
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.discovery.DiscoveryManagerListener
import com.connectsdk.discovery.provider.SSDPDiscoveryProvider
import com.connectsdk.service.DLNAService
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceListener
import com.github.heroslender.lgtvcontroller.device.LgDevice
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeviceManager(
    ctx: Context,
    private val scope: CoroutineScope,
    private val prefs: SettingsRepository
) : DiscoveryManagerListener {
    private var _connectedDevice: MutableStateFlow<Device?> = MutableStateFlow(null)
    val connectedDevice: StateFlow<Device?>
        get() = _connectedDevice

    private var hasConnected = false

    init {
        Log.d("Event_Listener", "restartDiscoveryManager :::: restart discovery")
        DiscoveryManager.init(ctx)
        DiscoveryManager.getInstance().pairingLevel = DiscoveryManager.PairingLevel.ON
        DiscoveryManager.getInstance().registerDeviceService(
            WebOSTVService::class.java,
            SSDPDiscoveryProvider::class.java
        )
        DiscoveryManager.getInstance().registerDeviceService(
            DLNAService::class.java,
            SSDPDiscoveryProvider::class.java
        )
        DiscoveryManager.getInstance().addListener(this)
        DiscoveryManager.getInstance().start()
    }

    fun setDeviceDisconnected() {
        _connectedDevice.tryEmit(null)
    }

    override fun onDeviceAdded(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d(
            "Device_Manager",
            "onDeviceAdded :::: id: ${device.id} :: ${device.friendlyName}; ${device.modelNumber}"
        )

        scope.launch {
            val favorite = prefs.settingsFlow.first().favoriteId
            if (!hasConnected
                && connectedDevice.value == null
                && favorite == device.id
                && device.getServiceByName(WebOSTVService.ID) != null
            ) {
                Log.d("Device_Manager", "onDeviceAdded :::: Connecting to device favorite")
                connect(device)
            }
        }
    }

    override fun onDeviceUpdated(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceUpdated :::: ${device.friendlyName}")

        scope.launch {
            val favorite = prefs.settingsFlow.first().favoriteId
            if (!hasConnected
                && connectedDevice.value == null
                && favorite == device.id
                && device.getServiceByName(WebOSTVService.ID) != null
            ) {
                Log.d("Device_Manager", "onDeviceAdded :::: Connecting to device favorite")
                connect(device)
            }
        }
    }

    override fun onDeviceRemoved(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceRemoved :::: ${device.friendlyName}")
    }

    override fun onDiscoveryFailed(manager: DiscoveryManager, error: ServiceCommandError) {
        Log.d("Device_Manager", "onDiscoveryFailed :::: ${error.message}")
    }

    fun connect(cDevice: ConnectableDevice) {
        if (cDevice.getServiceByName(WebOSTVService.ID) == null) {
            return
        }

        val device = LgDevice(cDevice)
        cDevice.addListener(DeviceListener(this, device))
        cDevice.connect()

        _connectedDevice.value = device
        hasConnected = true
    }
}