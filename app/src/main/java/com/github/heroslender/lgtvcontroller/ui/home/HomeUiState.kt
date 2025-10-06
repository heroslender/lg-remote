package com.github.heroslender.lgtvcontroller.ui.home

import com.github.heroslender.lgtvcontroller.device.DeviceControllerButton
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input

data class HomeUiState(
    val deviceID: String? = null,
    val deviceName: String? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val apps: List<App> = emptyList(),
    val inputs: List<Input> = emptyList(),
    val runningApp: String = "",
    val hasCapability: (DeviceControllerButton) -> Boolean = { false },
    val executeButton: (DeviceControllerButton) -> Unit = {},
    val sendBackspace: () -> Unit = {},
    val sendEnter: () -> Unit = {},
    val sendText: (String) -> Unit = {},
    val launchApp: (String) -> Unit = {},
)