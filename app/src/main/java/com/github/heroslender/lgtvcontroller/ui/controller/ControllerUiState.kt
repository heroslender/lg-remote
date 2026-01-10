package com.github.heroslender.lgtvcontroller.ui.controller

import com.github.heroslender.lgtvcontroller.device.DeviceControllerButton
import com.github.heroslender.lgtvcontroller.device.DeviceStatus

data class ControllerUiState(
    val deviceName: String? = null,
    val deviceStatus: DeviceStatus = DeviceStatus.DISCONNECTED,
    val clickMouse: () -> Unit = {},
    val moveMouse: (Double, Double) -> Unit = { _, _ -> },
    val scroll: (Double, Double) -> Unit = { _, _ -> },
    val hasCapability: (DeviceControllerButton) -> Boolean = { false },
    val executeButton: (DeviceControllerButton) -> Unit = {},
)