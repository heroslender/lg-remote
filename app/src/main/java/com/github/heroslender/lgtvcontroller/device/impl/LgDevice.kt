package com.github.heroslender.lgtvcontroller.device.impl

import com.connectsdk.core.AppInfo
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.capability.Launcher
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.json.JSONArray

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

    private val _inputs: MutableStateFlow<List<App>> =
        MutableStateFlow(tv.inputs)
    override val inputs: StateFlow<List<App>>
        get() = _inputs

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

    suspend fun loadAppsAndSources() {
        val launchPointsState = MutableStateFlow<List<App>>(emptyList())
        val webOSTVService = device.device.getServiceByName(WebOSTVService.ID) as WebOSTVService
        webOSTVService.getLaunchPoints(object : WebOSTVService.LaunchPointsListener {
            override fun onSuccess(arr: JSONArray) {
                val list = mutableListOf<App>()
                for (i in 0 until arr.length()) {
                    val raw = arr.getJSONObject(i)
                    if (raw.getBoolean("systemApp"))
                        continue

                    list.add(
                        App(
                            id = raw.getString("id"),
                            name = raw.getString("title"),
                            icon = raw.getString("icon"),
                            iconLarge = raw.getString("largeIcon"),
                        )
                    )
                }

                launchPointsState.value = list
            }

            override fun onError(error: ServiceCommandError) {
                TODO("Not yet implemented")
            }
        })

        data class AppListApp(
            val id: String,
            val name: String,
            val systemApp: Boolean,
            val installedTime: Long,
        )

        val appListState = MutableStateFlow<List<AppListApp>>(emptyList())
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

                appListState.value = appList
            }

            override fun onError(error: ServiceCommandError?) {
                TODO("Not yet implemented")
            }
        })

        combine(launchPointsState, appListState) { launchPointsFound, appListFound ->
            if (launchPointsFound.isEmpty() || appListFound.isEmpty()) {
                return@combine
            }

            val appList = mutableListOf<App>()
            val inputList = mutableListOf<App>()

            launchPointsFound.forEach { app ->
                val found = appListFound.firstOrNull { it.id == app.id }
                if (found != null) {
                    val appInfo = app.copy(installedTime = found.installedTime)
                    if (found.systemApp) {
                        inputList.add(appInfo)
                    } else {
                        appList.add(appInfo)
                    }
                }
            }

            appList.sortByDescending { it.installedTime }

            tv.apps = appList
            tv.inputs = inputList
            _apps.value = appList
            _inputs.value = inputList

            manager.updateTv(tv)
        }.collect()
    }

    fun updateDisplayName() {
        _displayName.tryEmit(tv.displayName)
    }
}