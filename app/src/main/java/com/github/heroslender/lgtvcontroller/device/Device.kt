package com.github.heroslender.lgtvcontroller.device

import kotlinx.coroutines.flow.Flow

interface Device {
    val id: String

    val friendlyName: String
    val displayName: String?

    val status: Flow<DeviceStatus>

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