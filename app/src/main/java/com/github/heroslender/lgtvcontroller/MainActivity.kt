package com.github.heroslender.lgtvcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.heroslender.lgtvcontroller.controller.ControllerScreen
import com.github.heroslender.lgtvcontroller.devicelist.DeviceListScreen
import com.github.heroslender.lgtvcontroller.editor.TvEditScreen
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LGTVControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavDest.DeviceList.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavDest.DeviceList.route) {
                            DeviceListScreen(
                                navigateToController = {
                                    navController.navigate(NavDest.Controller.route) {
                                        popUpTo(NavDest.DeviceList.route) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(NavDest.Controller.route) {
                            ControllerScreen(
                                navigateToDeviceList = {
                                    navController.navigate(NavDest.DeviceList.route)
                                },
                                navigateToEditDevice = {
                                    navController.navigate("${NavDest.EditDevice.route}/$it")
                                },
                            )
                        }

                        composable(
                            route = NavDest.EditDevice.routeWithArgs,
                            arguments = listOf(navArgument(NavDest.EditDevice.tvIdArg) {
                                type = NavType.StringType
                            }),
                        ) {
                            TvEditScreen(
                                navigateBack = {
                                    navController.navigate(NavDest.Controller.route) {
                                        popUpTo(NavDest.DeviceList.route) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class NavDest(val route: String) {
    data object DeviceList : NavDest("device_list")
    data object EditDevice : NavDest("device_edit") {
        const val tvIdArg = "tvId"
        val routeWithArgs = "$route/{$tvIdArg}"
    }
    data object Controller : NavDest("controller")

    override fun toString(): String {
        return route
    }
}