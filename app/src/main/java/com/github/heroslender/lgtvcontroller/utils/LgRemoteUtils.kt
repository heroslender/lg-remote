package com.github.heroslender.lgtvcontroller.utils

import com.connectsdk.service.WebOSTVService
import com.connectsdk.service.capability.listeners.ResponseListener

fun WebOSTVService.sendSpecialKey(key: String) {
    val method = WebOSTVService::class.java.getDeclaredMethod(
        "sendSpecialKey",
        String::class.java,
        ResponseListener::class.java
    )
    method.isAccessible = true
    method.invoke(this, key, null)
}