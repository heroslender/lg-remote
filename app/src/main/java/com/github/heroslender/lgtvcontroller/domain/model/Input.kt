package com.github.heroslender.lgtvcontroller.domain.model

data class Input(
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
    val connected: Boolean = false,
    val favorite: Boolean = false,
)
