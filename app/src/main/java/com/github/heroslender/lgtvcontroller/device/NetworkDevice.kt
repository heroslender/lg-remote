package com.github.heroslender.lgtvcontroller.device

interface NetworkDevice {
    val id: String
    val friendlyName: String
    val status: DeviceStatus

    fun connect()
}