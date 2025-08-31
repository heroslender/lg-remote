package com.github.heroslender.lgtvcontroller.device

import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Device {
    val tv: Tv

    val id: String
        get() = tv.id

    val friendlyName: String
        get() = tv.name

    val state: StateFlow<DeviceState>

    val errors: Flow<Snackbar>

    fun hasCapability(capability: String): Boolean

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