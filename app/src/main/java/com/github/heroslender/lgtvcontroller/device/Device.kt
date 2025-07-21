package com.github.heroslender.lgtvcontroller.device

import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import kotlinx.coroutines.flow.Flow

interface Device {
    val tv: Tv

    val id: String
        get() = tv.id

    val friendlyName: String
        get() = tv.name
    val displayName: Flow<String?>

    val status: Flow<DeviceStatus>

    val apps: Flow<List<App>>
    val inputs: Flow<List<Input>>

    val runningApp: Flow<String>

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