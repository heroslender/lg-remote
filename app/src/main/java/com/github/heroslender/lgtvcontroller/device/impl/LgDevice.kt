package com.github.heroslender.lgtvcontroller.device.impl

import android.util.Log
import androidx.room.util.copy
import com.connectsdk.core.AppInfo
import com.connectsdk.core.ExternalInputInfo
import com.connectsdk.core.TextInputStatusInfo
import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.capability.ExternalInputControl
import com.connectsdk.service.capability.Launcher
import com.connectsdk.service.capability.TextInputControl
import com.connectsdk.service.command.ServiceCommandError
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceState
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LgDevice(
    val device: LgNetworkDevice,
    private val manager: DeviceManager,
    status: DeviceStatus = DeviceStatus.DISCONNECTED,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : Device {
    override val tv: Tv
        get() = device.tv

    private val _state: MutableStateFlow<DeviceState> = MutableStateFlow(
        DeviceState(
            displayName = tv.displayName,
            status = status,
            runningApp = "",
            apps = tv.apps,
            inputs = tv.inputs,
            isKeyboardOpen = false,
        )
    )
    override val state: StateFlow<DeviceState>
        get() = _state

    private val _errors = MutableSharedFlow<Snackbar>()
    override val errors: Flow<Snackbar> = _errors

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
        _state.update {
            it.copy(status = status)
        }
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
                scope.launch {
                    _errors.emit(Snackbar.error("Failed to load inputs from TV", error.message))
                }
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
                                systemApp = it.rawData.optBoolean("systemApp", false),
                                installedTime = it.rawData.optLong("installedTime", 0L),
                            )
                        )
                    }

                    cont.resume(appList)
                }

                override fun onError(error: ServiceCommandError) {
                    scope.launch {
                        _errors.emit(
                            Snackbar.error(
                                "Failed to load app list from TV",
                                error.message
                            )
                        )
                    }
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
                        if (raw.optBoolean("systemApp", false))
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
                    scope.launch {
                        _errors.emit(
                            Snackbar.error(
                                "Failed to load launch points from TV",
                                error.message
                            )
                        )
                    }
                    continuation.resume(emptyList())
                }
            })
        }
    }

    fun subscribeRunningApp() {
        device.device.launcher.subscribeRunningApp(object : Launcher.AppInfoListener {
            override fun onSuccess(appInfo: AppInfo) {
                _state.update { it.copy(runningApp = appInfo.id) }
            }

            override fun onError(error: ServiceCommandError) {
            }
        })
    }

    override fun sendText(text: String) {
     device.device.textInputControl.sendText(text)
    }

    override fun sendEnter() {
        device.device.textInputControl.sendEnter()
    }

    override fun sendDelete() {
        device.device.textInputControl.sendDelete()
    }

    fun subscribeTextInputListener() {
        val textInputControl = device.device.getCapability(TextInputControl::class.java)
        textInputControl.subscribeTextInputStatus(object : TextInputControl.TextInputStatusListener {
            override fun onSuccess(statusInfo: TextInputStatusInfo) {
                if (statusInfo.contentType != null) {
                    _state.update { it.copy(isKeyboardOpen = true) }
                } else {
                    _state.update { it.copy(isKeyboardOpen = false) }
                }

                println("TextInputStatusListener: ${statusInfo.rawData.toString(2)}")
            }

            override fun onError(error: ServiceCommandError) {
                println("TextInputStatusListener error: $error")
            }
        })
    }

    val TextInputStatusInfo.contentType: String?
        get() {
            return this::class.java.getDeclaredField("contentType").let { field ->
                field.isAccessible = true
                return@let field[this] as String?
            }
        }

    suspend fun loadAppsAndInputs() {
        try {
            val inputList = getExternalInputList()
            tv.inputs = inputList
            _state.update { it.copy(inputs = inputList) }
        } catch (e: Exception) {
            tv.inputs = emptyList()
            _state.update { it.copy(inputs = emptyList()) }
            Log.e("LgDevice", "Failed to load inputs: ${e.message}")
            _errors.emit(Snackbar.error("Failed to load inputs", e.message))
        }

        try {
            val appList = getAppList().sortedByDescending { it.installedTime }
            tv.apps = appList
            _state.update { it.copy(apps = appList) }
        } catch (e: Exception) {
            tv.apps = emptyList()
            _state.update { it.copy(apps = emptyList()) }
            Log.e("LgDevice", "Failed to load apps: ${e.message}")
            _errors.emit(Snackbar.error("Failed to load apps", e.message))
        }

        manager.updateTv(tv)

        try {
            subscribeRunningApp()
        } catch (e: Exception) {
            Log.e("LgDevice", "Failed to subscribe to running app: ${e.message}")
            _errors.emit(Snackbar.error("Failed to subscribe to running app", e.message))
        }

        try {
            subscribeTextInputListener()
        } catch (e: Exception) {
            Log.e("LgDevice", "Failed to subscribe to text input: ${e.message}")
            _errors.emit(Snackbar.error("Failed to subscribe to text input", e.message))
        }
    }

    fun updateDisplayName() {
        _state.update { it.copy(displayName = tv.displayName) }
    }
}