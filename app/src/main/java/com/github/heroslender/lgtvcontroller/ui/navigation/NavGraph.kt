package com.github.heroslender.lgtvcontroller.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.heroslender.lgtvcontroller.ui.controller.ControllerScreen
import com.github.heroslender.lgtvcontroller.ui.devicelist.DeviceListScreen
import com.github.heroslender.lgtvcontroller.ui.editor.TvEditScreen

@Composable
fun ControllerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavDest.DeviceList.route,
        modifier = modifier
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