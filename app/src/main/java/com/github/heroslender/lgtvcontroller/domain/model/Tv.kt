package com.github.heroslender.lgtvcontroller.domain.model

data class Tv(
    val id: String,
    val name: String = "",
    var displayName: String? = "",
    var apps: List<App> = emptyList(),
    var inputs: List<App> = emptyList(),
)