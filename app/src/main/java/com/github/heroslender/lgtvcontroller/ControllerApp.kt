package com.github.heroslender.lgtvcontroller;

import androidx.compose.runtime.Composable;
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.heroslender.lgtvcontroller.ui.navigation.ControllerNavHost

@Composable
fun ControllerApp(navController: NavHostController = rememberNavController()) {
    ControllerNavHost(navController)
}
