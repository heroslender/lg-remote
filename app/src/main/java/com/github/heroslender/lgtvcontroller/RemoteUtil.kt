package com.github.heroslender.lgtvcontroller

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.DevicePicker
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.discovery.DiscoveryManagerListener
import com.connectsdk.discovery.provider.SSDPDiscoveryProvider
import com.connectsdk.service.DLNAService
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.device.Device

private const val AUTOCONNECT_ID = "AutoConnectId"

open class RemoteUtil(val prefs: SharedPreferences) : DiscoveryManagerListener {
    companion object {
        lateinit var instance: RemoteUtil
    }

    private var _connectedDevice: MutableState<Device?> = mutableStateOf(null)
    val connectedDevice: State<Device?>
        get() = _connectedDevice

    private var hasConnected = false

    var favorite: String
        get() = prefs.getString(AUTOCONNECT_ID, "")!!
        set(value) {
            prefs.edit().apply {
                putString(AUTOCONNECT_ID, value)
                commit()
            }
        }

    init {
        instance = this
    }

    fun setDeviceDisconnected() {
        _connectedDevice.value = null
    }

    lateinit var activity: Activity

    fun discover(activity: Activity) {
        this.activity = activity
        Log.d("Event_Listener", "restartDiscoveryManager :::: restart discovery")
        DiscoveryManager.init(activity)
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

    override fun onDeviceAdded(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d(
            "Device_Manager",
            "onDeviceAdded :::: id: ${device.id} :: ${device.friendlyName}; ${device.modelNumber}"
        )

        if (!hasConnected
            && this.connectedDevice.value == null
            && favorite == device.id
            && device.getServiceByName(WebOSTVService.ID) == null
        ) {
            Log.d("Device_Manager", "onDeviceAdded :::: Connecting to device favorite")
            connect(device)
        }
    }

    override fun onDeviceUpdated(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceUpdated :::: ${device.friendlyName}")

        if (!hasConnected
            && this.connectedDevice.value == null
            && favorite == device.id
            && device.getServiceByName(WebOSTVService.ID) == null
        ) {
            Log.d("Device_Manager", "onDeviceAdded :::: Connecting to device favorite")
            connect(device)
        }
    }

    override fun onDeviceRemoved(manager: DiscoveryManager, device: ConnectableDevice) {
        Log.d("Device_Manager", "onDeviceRemoved :::: ${device.friendlyName}")
    }

    override fun onDiscoveryFailed(manager: DiscoveryManager, error: ServiceCommandError) {
        Log.d("Device_Manager", "onDiscoveryFailed :::: ${error.message}")
    }

    fun listDevices() {
        val devicePicker = DevicePicker(activity)
        val selectDevice = AdapterView.OnItemClickListener { adapter, parent, position, id ->
            Log.d("Device_Manager", "listDevices :::: connecting to device")
            val cDevice = adapter.getItemAtPosition(position) as ConnectableDevice
            connect(cDevice)
        }
        val dialog: AlertDialog =
            devicePicker.getPickerDialog("Devices", selectDevice)

        dialog.show()
    }

    fun connect(cDevice: ConnectableDevice) {
        if (cDevice.getServiceByName(WebOSTVService.ID) == null) {
            Toast.makeText(activity, "Cannot connect to this", Toast.LENGTH_SHORT).show()
            return
        }
        val device = Device(cDevice)
        cDevice.addListener(device)
        cDevice.connect()

        _connectedDevice.value = device
        hasConnected = true
    }
}