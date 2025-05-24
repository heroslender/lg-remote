package com.github.heroslender.lgtvcontroller.ui.navigation

/**
 * Each instance represents a destination in the nav graph
 */
sealed class NavDest(
    /**
     * Route for this destination
     */
    val route: String,
) {
    data object DeviceList : NavDest("device_list")
    data object EditDevice : NavDest("device_edit") {
        const val tvIdArg = "tvId"
        override val routeWithArgs = "$route/{$tvIdArg}"
    }
    data object Controller : NavDest("controller")

    /**
     * Route for this destination if it contains arguments
     */
    open val routeWithArgs: String = ""

    override fun toString(): String {
        return route
    }
}