package com.github.heroslender.lgtvcontroller.device.impl

import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceState
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A fake device used for compose component previews.
 */
object PreviewDevice : Device {
    override val id: String = "gads-sdgfds-g-fdsgfdgdf-sdfsdf"
    override val friendlyName: String = "Living Room TV"
    override val tv: Tv = Tv(id, friendlyName, "Living Room TV", false, emptyList(), emptyList())
    override val state: Flow<DeviceState> =
        flowOf(
            DeviceState(
                displayName = "Living Room TV",
                status = DeviceStatus.CONNECTED,
                runningApp = "",
                apps = emptyList(),
                inputs = emptyList(),
                isKeyboardOpen = true,
            )
        )
    override val errors: Flow<Snackbar> = flowOf()

    override fun hasCapability(capability: String): Boolean = true
    override fun powerOff() {}
    override fun volumeUp() {}
    override fun volumeDown() {}
    override fun mute() {}
    override fun channelUp() {}
    override fun channelDown() {}
    override fun up() {}
    override fun down() {}
    override fun left() {}
    override fun right() {}
    override fun ok() {}
    override fun back() {}
    override fun exit() {}
    override fun home() {}
    override fun info() {}
    override fun source() {}
    override fun menu() {}
    override fun qmenu() {}
    override fun mouseClick() {}
    override fun moveMouse(x: Double, y: Double) {}
    override fun scroll(x: Double, y: Double) {}
    override fun launchNetflix() {}
    override fun launchApp(appId: String) {}
    override fun sendText(text: String) {}
    override fun sendEnter() {}
    override fun sendDelete() {}
    override fun disconnect() {}
    override fun updateStatus(status: DeviceStatus) {}
}