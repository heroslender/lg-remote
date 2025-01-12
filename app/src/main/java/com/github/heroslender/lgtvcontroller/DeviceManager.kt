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
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.NetworkDevice
import com.github.heroslender.lgtvcontroller.device.impl.LgDevice
import com.github.heroslender.lgtvcontroller.device.impl.LgNetworkDevice
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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

    private var _devices: MutableStateFlow<List<NetworkDevice>> = MutableStateFlow(emptyList())
    val devices: StateFlow<List<NetworkDevice>>
        get() = _devices

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

        scope.launch {
            while (!hasConnected) {
                for (discoveryProvider in DiscoveryManager.getInstance().discoveryProviders) {
                    Log.d("Device_Manager", "rescan :::: " + discoveryProvider::class.simpleName)
                    discoveryProvider.rescan()
                }

                delay(2000)
            }
        }
    }

    private fun getDevice(id: String): NetworkDevice? {
        for (device in devices.value) {
            if (device.id == id) {
                return device
            }
        }

        return null
    }

    fun onDeviceConnected(device: Device) {
        val networkDevice = getDevice(device.id) ?: return
        networkDevice.updateStatus(DeviceStatus.CONNECTED)
    }

    fun onDeviceDisconnected(device: Device) {
        val networkDevice = getDevice(device.id) ?: return
        networkDevice.updateStatus(DeviceStatus.DISCONNECTED)
    }

    fun onDevicePairing(device: Device) {
        val networkDevice = getDevice(device.id) ?: return
        networkDevice.updateStatus(DeviceStatus.PAIRING)
    }

    fun setDeviceDisconnected() {
        _connectedDevice.tryEmit(null)
    }

    fun ConnectableDevice.isCompatible(): Boolean {
        return getServiceByName(WebOSTVService.ID) != null
    }

    private fun autoConnect(device: ConnectableDevice) {
        if (hasConnected || !device.isCompatible()) {
            return
        }

        scope.launch {
            val favorite = prefs.settingsFlow.first().favoriteId
            if (!hasConnected && favorite == device.id && device.isCompatible()) {
                Log.d("Device_Manager", "Connecting to device favorite")
                connect(device)
            }
        }
    }

    override fun onDeviceAdded(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d(
            "Device_Manager",
            "onDeviceAdded :::: id: ${device.id} :: ${device.friendlyName}; ${device.modelNumber}"
        )

        autoConnect(device)

        if (device.isCompatible()) {
            _devices.tryEmit(listOf(*devices.value.toTypedArray(), device.toNetworkDevice()))
        }
    }

    override fun onDeviceUpdated(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceUpdated :::: ${device.friendlyName}")

        autoConnect(device)

        if (device.isCompatible()) {
            val devices = devices.value.toTypedArray()
            if (devices.indexOfFirst { it.id == device.id } == -1) {
                _devices.tryEmit(listOf(*devices, device.toNetworkDevice()))
            }
        }
    }

    override fun onDeviceRemoved(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceRemoved :::: ${device.friendlyName}")
        _devices.tryEmit(devices.value.toMutableList().apply { removeIf { it.id == device.id } })
    }

    override fun onDiscoveryFailed(manager: DiscoveryManager, error: ServiceCommandError) {
        Log.d("Device_Manager", "onDiscoveryFailed :::: ${error.message}")
    }

    fun connect(cDevice: ConnectableDevice) {
        if (cDevice.getServiceByName(WebOSTVService.ID) == null) {
            return
        }

        val device = LgDevice(cDevice, DeviceStatus.CONNECTING)
        cDevice.addListener(DeviceListener(this, device))
        cDevice.connect()

        _connectedDevice.value = device
        hasConnected = true
    }

    private fun ConnectableDevice.toNetworkDevice(): NetworkDevice {
        return LgNetworkDevice(
            id = id,
            friendlyName = friendlyName,
            status = if (isConnected) DeviceStatus.CONNECTED else DeviceStatus.DISCONNECTED,
            connectToDevice = {
                connect(this)
            }
        )
    }
}