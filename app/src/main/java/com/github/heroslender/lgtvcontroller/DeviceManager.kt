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
import com.github.heroslender.lgtvcontroller.storage.Tv
import com.github.heroslender.lgtvcontroller.storage.TvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeviceManager(
    ctx: Context,
    private val scope: CoroutineScope,
    private val prefs: SettingsRepository,
    private val tvRepository: TvRepository,
) : DiscoveryManagerListener {
    private var _connectedDevice: MutableStateFlow<LgDevice?> = MutableStateFlow(null)
    val connectedDevice: StateFlow<Device?>
        get() = _connectedDevice

    private var _devices: MutableStateFlow<List<NetworkDevice>> = MutableStateFlow(emptyList())
    val devices: StateFlow<List<NetworkDevice>>
        get() = _devices

    private var hasConnected = false

    /**
     * Whether the app is running in the background or not.
     * This is because when the app is in the background, it looses connection to the internet,
     * and therefore, disconnects from the tv. This is used to reconnect back to the tv when the
     * app returns to the foreground.
     */
    private var isAppPaused = false

    /**
     * The id of the tv that was connected when the app was paused.
     */
    private var connectedTvId: String? = null

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
            try {
                while (!hasConnected) {
                    for (discoveryProvider in DiscoveryManager.getInstance().discoveryProviders) {
                        Log.d(
                            "Device_Manager",
                            "rescan :::: " + discoveryProvider::class.simpleName
                        )
                        discoveryProvider.rescan()
                    }

                    delay(2000)
                }
            } catch (e: Exception) {
                Log.e("Device_Manager", "rescan :::: " + e.message)
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

        scope.launch {
            (device as LgDevice).loadAppsAndSources()
        }
    }

    fun onDeviceDisconnected(device: Device) {
        if (isAppPaused) {
            this.connectedTvId = device.id
        }

        val networkDevice = getDevice(device.id) ?: return
        networkDevice.updateStatus(DeviceStatus.DISCONNECTED)

        val dev = _connectedDevice.value ?: return
        if (!dev.device.device.isConnecting) {
            _connectedDevice.tryEmit(null)
        }
    }

    fun onDevicePairing(device: Device) {
        val networkDevice = getDevice(device.id) ?: return
        networkDevice.updateStatus(DeviceStatus.PAIRING)
    }

    fun ConnectableDevice.isCompatible(): Boolean {
        return getServiceByName(WebOSTVService.ID) != null
    }

    fun LgNetworkDevice.isCompatible() = device.isCompatible()

    override fun onDeviceAdded(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d(
            "Device_Manager",
            "onDeviceAdded :::: id: ${device.id} :: ${device.friendlyName}; ${device.modelNumber}"
        )

        if (device.isCompatible()) {
            deviceFound(device)
        }
    }

    override fun onDeviceUpdated(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceUpdated :::: ${device.friendlyName}")

        if (device.isCompatible() && devices.value.indexOfFirst { it.id == device.id } == -1) {
            deviceFound(device)
        }
    }

    override fun onDeviceRemoved(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceRemoved :::: ${device.friendlyName}")
        _devices.tryEmit(devices.value.toMutableList().apply { removeIf { it.id == device.id } })
    }

    override fun onDiscoveryFailed(manager: DiscoveryManager, error: ServiceCommandError) {
        Log.d("Device_Manager", "onDiscoveryFailed :::: ${error.message}")
    }

    fun deviceFound(device: ConnectableDevice) {
        scope.launch {
            val tv = try {
                tvRepository
                    .getTvStream(device.id)
                    .first()
            } catch (_: IllegalStateException) {
                Log.d("DeviceManager", "Device not found in database")
                null
            }

            val networkDevice = LgNetworkDevice(
                device = device,
                tv = tv,
                manager = this@DeviceManager,
            )

            _devices.tryEmit(listOf(*devices.value.toTypedArray(), networkDevice))

            autoConnect(networkDevice)
        }
    }

    fun connect(cDevice: LgNetworkDevice) {
        if (cDevice.device.getServiceByName(WebOSTVService.ID) == null) {
            return
        }

        val currDevice = connectedDevice.value
        if (currDevice != null) {
            if (currDevice.id == cDevice.id) {
                // Already connected to this device
                return
            }

            currDevice.disconnect()
        }

        val device = LgDevice(cDevice, DeviceStatus.CONNECTING)
        cDevice.device.addListener(DeviceListener(this, device))
        cDevice.device.connect()

        _connectedDevice.value = device
        hasConnected = true

        scope.launch {
            tvRepository.insertTv(Tv(device.id, device.friendlyName, ""))
        }
    }

    private fun autoConnect(device: LgNetworkDevice) {
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

    fun tvUpdated(tv: Tv) {
        devices.value.first { it.id == tv.id }.displayName = tv.displayName
    }

    fun resume() {
        isAppPaused = false

        val connectedTvId = connectedTvId ?: return
        this.connectedTvId = null

        if (connectedDevice.value != null) {
            return
        }

        val networkDevice = getDevice(connectedTvId) ?: return
        networkDevice.connect()
    }

    fun pause() {
        isAppPaused = true
    }
}