package com.github.heroslender.lgtvcontroller.device

import com.connectsdk.service.capability.ExternalInputControl
import com.connectsdk.service.capability.KeyControl
import com.connectsdk.service.capability.PowerControl
import com.connectsdk.service.capability.TVControl
import com.connectsdk.service.capability.VolumeControl
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow

interface Device {
    val tv: Tv

    val id: String
        get() = tv.id

    val friendlyName: String
        get() = tv.name

    val state: Flow<DeviceState>

    val errors: Flow<Snackbar>

    fun hasCapability(btn: DeviceControllerButton): Boolean {
        when (btn) {
            DeviceControllerButton.POWER -> return hasCapability(PowerControl.Any)
            DeviceControllerButton.VOLUME_UP,
            DeviceControllerButton.VOLUME_DOWN,
                -> return hasCapability(VolumeControl.Volume_Up_Down)

            DeviceControllerButton.MUTE -> return hasCapability(VolumeControl.Mute_Set)
            DeviceControllerButton.CHANNEL_UP -> return hasCapability(TVControl.Channel_Up)
            DeviceControllerButton.CHANNEL_DOWN -> return hasCapability(TVControl.Channel_Down)
            DeviceControllerButton.UP -> return hasCapability(KeyControl.Up)
            DeviceControllerButton.DOWN -> return hasCapability(KeyControl.Down)
            DeviceControllerButton.LEFT -> return hasCapability(KeyControl.Left)
            DeviceControllerButton.RIGHT -> return hasCapability(KeyControl.Right)
            DeviceControllerButton.OK -> return hasCapability(KeyControl.OK)
            DeviceControllerButton.BACK,
            DeviceControllerButton.EXIT,
                -> return hasCapability(KeyControl.Back)

            DeviceControllerButton.HOME -> return hasCapability(KeyControl.Home)
            DeviceControllerButton.INFO -> return hasCapability(KeyControl.Any)
            DeviceControllerButton.SOURCE -> return hasCapability(ExternalInputControl.Any)
            DeviceControllerButton.MENU -> return hasCapability(KeyControl.Any)
            DeviceControllerButton.QMENU -> return hasCapability(KeyControl.Any)
        }
    }

    fun hasCapability(capability: String): Boolean

    fun executeControllerButton(btn: DeviceControllerButton) {
        when (btn) {
            DeviceControllerButton.POWER -> powerOff()
            DeviceControllerButton.VOLUME_UP -> volumeUp()
            DeviceControllerButton.VOLUME_DOWN -> volumeDown()
            DeviceControllerButton.MUTE -> mute()
            DeviceControllerButton.CHANNEL_UP -> channelUp()
            DeviceControllerButton.CHANNEL_DOWN -> channelDown()
            DeviceControllerButton.UP -> up()
            DeviceControllerButton.DOWN -> down()
            DeviceControllerButton.LEFT -> left()
            DeviceControllerButton.RIGHT -> right()
            DeviceControllerButton.OK -> ok()
            DeviceControllerButton.BACK -> back()
            DeviceControllerButton.EXIT -> exit()
            DeviceControllerButton.HOME -> home()
            DeviceControllerButton.INFO -> info()
            DeviceControllerButton.SOURCE -> source()
            DeviceControllerButton.MENU -> menu()
            DeviceControllerButton.QMENU -> qmenu()
        }
    }

    fun powerOff()

    fun volumeUp()

    fun volumeDown()

    fun mute()

    fun channelUp()

    fun channelDown()

    fun up()

    fun down()

    fun left()

    fun right()

    fun ok()

    fun back()

    fun exit()

    fun home()

    fun info()

    fun source()

    fun menu()

    fun qmenu()

    fun launchNetflix()

    fun launchApp(appId: String)
    fun disconnect()
    fun updateStatus(status: DeviceStatus)
}