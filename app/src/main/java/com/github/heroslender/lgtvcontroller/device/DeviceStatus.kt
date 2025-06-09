package com.github.heroslender.lgtvcontroller.device

import com.github.heroslender.lgtvcontroller.R

enum class DeviceStatus(
    val nameResId: Int
) {
    CONNECTING(R.string.connecting),
    PAIRING(R.string.pairing),
    CONNECTED(R.string.connected),
    DISCONNECTED(R.string.disconnected)
}