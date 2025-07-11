package com.github.heroslender.lgtvcontroller.device

import androidx.compose.runtime.State
import com.github.heroslender.lgtvcontroller.domain.model.Tv

interface NetworkDevice {
    val id: String
    val friendlyName: String
    val tv: Tv
    val status: State<DeviceStatus>

    var displayName: String?
        get() = tv.displayName
        set(value) {
            tv.displayName = value
        }

    fun connect()

    fun updateStatus(status: DeviceStatus)
}