package com.github.heroslender.lgtvcontroller.ui.home

import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input

data class HomeUiState(
    val device: Device? = null,
    val deviceName: String? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val isFavorite: Boolean = false,
    val apps: List<App> = emptyList(),
    val inputs: List<Input> = emptyList(),
)