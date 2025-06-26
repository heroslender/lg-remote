package com.github.heroslender.lgtvcontroller.ui.home

import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.LgAppInfo

data class HomeUiState(
    val device: Device? = null,
    val deviceName: String? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val isFavorite: Boolean = false,
    val apps: List<LgAppInfo> = emptyList(),
    val inputs: List<LgAppInfo> = emptyList(),
)