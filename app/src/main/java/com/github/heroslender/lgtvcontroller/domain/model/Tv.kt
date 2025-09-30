package com.github.heroslender.lgtvcontroller.domain.model

data class Tv(
    val id: String,
    val name: String = "",
    var displayName: String? = "",
    var autoConnect: Boolean = false,
    var apps: List<App> = emptyList(),
    var inputs: List<Input> = emptyList(),
)