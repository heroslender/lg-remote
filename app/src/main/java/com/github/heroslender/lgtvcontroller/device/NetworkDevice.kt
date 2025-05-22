package com.github.heroslender.lgtvcontroller.device

import androidx.compose.runtime.State

interface NetworkDevice {
    val id: String
    val friendlyName: String
    var displayName: String?
    val status: State<DeviceStatus>

    fun connect()

    fun updateStatus(status: DeviceStatus)
}