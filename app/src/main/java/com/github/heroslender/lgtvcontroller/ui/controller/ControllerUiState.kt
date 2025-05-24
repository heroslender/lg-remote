package com.github.heroslender.lgtvcontroller.ui.controller

import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus

data class ControllerUiState(
    val device: Device? = null,
    val deviceName: String? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val isFavorite: Boolean = false,
)