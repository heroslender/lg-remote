package com.github.heroslender.lgtvcontroller.device

import kotlinx.coroutines.flow.Flow

interface Device {
    val id: String

    val friendlyName: String

    val status: Flow<DeviceStatus>

    fun powerOff()

    fun volumeUp()

    fun volumeDown()

    fun channelUp()

    fun channelDown()

    fun up()

    fun down()

    fun left()

    fun right()

    fun ok()

    fun back()

    fun home()

    fun info()

    fun source()

    fun launchNetflix()

    fun launchApp(appId: String)
    fun disconnect()
    fun updateStatus(status: DeviceStatus)
}