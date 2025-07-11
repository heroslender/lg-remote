package com.github.heroslender.lgtvcontroller.domain.model

data class App(
    /**
     * App ID
     */
    val id: String,
    /**
     * App title
     */
    val name: String,
    /**
     * App icon URL hosted by the TV, requires ssl check to be disabled
     * Icon size: 80x80
     */
    val icon: String = "",
    /**
     * App icon URL hosted by the TV, requires ssl check to be disabled
     * Icon size: 130x130
     */
    val iconLarge: String = "",
    /**
     * Timestamp the app was installed, 0 for pre-installed apps
     */
    val installedTime: Long = 0,
)
