package com.github.heroslender.lgtvcontroller.device.impl

import com.connectsdk.core.AppInfo
import com.connectsdk.core.ExternalInputInfo
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.capability.ExternalInputControl
import com.connectsdk.service.capability.Launcher
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LgDevice(
    val device: LgNetworkDevice,
    private val manager: DeviceManager,
    status: DeviceStatus = DeviceStatus.DISCONNECTED,
) : Device {
    override val tv: Tv
        get() = device.tv

    private val _status: MutableStateFlow<DeviceStatus> =
        MutableStateFlow(status)
    override val status: StateFlow<DeviceStatus>
        get() = _status

    private val _apps: MutableStateFlow<List<App>> =
        MutableStateFlow(tv.apps)
    override val apps: StateFlow<List<App>>
        get() = _apps

    private val _inputs: MutableStateFlow<List<Input>> =
        MutableStateFlow(tv.inputs)
    override val inputs: StateFlow<List<Input>>
        get() = _inputs

    private val _runningApp: MutableStateFlow<String> =
        MutableStateFlow("")
    override val runningApp: StateFlow<String>
        get() = _runningApp

    override val id: String
        get() = device.id

    override val friendlyName: String
        get() = tv.name

    private val _displayName: MutableStateFlow<String?> = MutableStateFlow(tv.displayName)
    override val displayName: Flow<String?>
        get() = _displayName

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

    override fun mute() {
//        service.setMute(true, null)
        service.sendSpecialKey("MUTE")
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

    override fun exit() {
        service.sendSpecialKey("EXIT")
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

    suspend fun getExternalInputList(): List<Input> = suspendCoroutine { continuation ->
        device.device.externalInputControl.getExternalInputList(object :
            ExternalInputControl.ExternalInputListListener {
            override fun onSuccess(discoveredInputs: List<ExternalInputInfo>) {
                val inputs = mutableListOf<Input>()
                for (info in discoveredInputs) {
                    inputs.add(
                        Input(
                            id = info.rawData.getString("appId"),
                            name = info.name,
                            icon = info.iconURL,
                            connected = info.isConnected,
                            favorite = info.rawData.optBoolean("favorite", false)
                        )
                    )
                }

                continuation.resume(inputs)
            }

            override fun onError(error: ServiceCommandError) {
                continuation.resume(emptyList())
            }
        })
    }

    suspend fun getAppList(): List<App> {
        data class AppListApp(
            val id: String,
            val name: String,
            val systemApp: Boolean,
            val installedTime: Long,
        )

        val appList: MutableList<AppListApp> = suspendCoroutine { cont ->
            device.device.launcher.getAppList(object : Launcher.AppListListener {
                override fun onSuccess(apps: List<AppInfo>) {
                    val appList = mutableListOf<AppListApp>()
                    apps.forEach {
                        appList.add(
                            AppListApp(
                                id = it.id,
                                name = it.name,
                                systemApp = it.rawData.getBoolean("systemApp"),
                                installedTime = it.rawData.getLong("installedTime"),
                            )
                        )
                    }

                    cont.resume(appList)
                }

                override fun onError(error: ServiceCommandError?) {
                    cont.resume(mutableListOf())
                }
            })
        }

        return suspendCoroutine { continuation ->
            val webOSTVService = device.device.getServiceByName(WebOSTVService.ID) as WebOSTVService
            webOSTVService.getLaunchPoints(object : WebOSTVService.LaunchPointsListener {
                override fun onSuccess(arr: JSONArray) {
                    val list = mutableListOf<App>()
                    for (i in 0 until arr.length()) {
                        val raw = arr.getJSONObject(i)
                        if (raw.getBoolean("systemApp"))
                            continue

                        val id = raw.getString("id")
                        val found = appList.firstOrNull { it.id == id }
                        if (found != null && !found.systemApp) {
                            list.add(
                                App(
                                    id = found.id,
                                    name = found.name,
                                    icon = raw.getString("largeIcon"),
                                    installedTime = found.installedTime,
                                )
                            )
                        }
                    }

                    continuation.resume(list)
                }

                override fun onError(error: ServiceCommandError) {
                    continuation.resume(emptyList())
                }
            })
        }
    }

    fun subscribeRunningApp() {
        device.device.launcher.subscribeRunningApp(object : Launcher.AppInfoListener {
            override fun onSuccess(appInfo: AppInfo) {
                _runningApp.tryEmit(appInfo.id)
            }

            override fun onError(error: ServiceCommandError) {
            }
        })
    }

    suspend fun loadAppsAndInputs() {
        val inputList = getExternalInputList()
        tv.inputs = inputList
        _inputs.value = inputList

        val appList = getAppList().sortedByDescending { it.installedTime }
        tv.apps = appList
        _apps.value = appList

        manager.updateTv(tv)

        subscribeRunningApp()
    }

    fun updateDisplayName() {
        _displayName.tryEmit(tv.displayName)
    }
}