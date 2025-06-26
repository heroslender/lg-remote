package com.github.heroslender.lgtvcontroller;

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.ui.navigation.ControllerNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun ControllerApp(navController: NavHostController = rememberNavController()) {
    ControllerNavHost(navController)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ControllerTopAppBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    titleHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: (() -> Unit)? = null,
) {
    val navigationIcon: @Composable () -> Unit = {
        if (navigateUp != null) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(string.back_button),
                )
            }
        }
    }

    if (subtitle != null) {
        TopAppBar(
            title = { Text(title) },
            subtitle = { Text(subtitle) },
            titleHorizontalAlignment = titleHorizontalAlignment,
            modifier = modifier,
            actions = actions,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon,
        )
    } else {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
            actions = actions,
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon ,
        )
    }
}

@Composable
fun TopAppBarAction(
    imageVector: ImageVector,
    contentDescription: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
    }
}