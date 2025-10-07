@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.github.heroslender.lgtvcontroller.ui.devicelist

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.heroslender.lgtvcontroller.R
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.ui.ConnectedDeviceScaffold
import com.github.heroslender.lgtvcontroller.ui.controller.CButton
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import kotlin.math.min
import kotlin.math.pow

@Preview(
    group = "no devices",
    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDeviceList() {
    LGTVControllerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SharedTransitionLayout(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(true, label = "found devices transition") { _ ->
                    NoDevices(
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
        }
    }
}

@Preview(
    group = "has devices",
    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
@SuppressLint("UnrememberedMutableState")
fun PreviewDeviceListFound() {
    val devices = listOf(
        DeviceItemData(
            displayName = "Your Awesome TV 1",
            status = mutableStateOf(DeviceStatus.DISCONNECTED),
            isPoweredOn = true,
            connect = {}
        ),
        DeviceItemData(
            displayName = "Your Awesome TV 2",
            status = mutableStateOf(DeviceStatus.CONNECTED),
            isPoweredOn = true,
            connect = {}
        ),
        DeviceItemData(
            displayName = "Your Awesome TV 3",
            status = mutableStateOf(DeviceStatus.DISCONNECTED),
            isPoweredOn = false,
            connect = {}
        )
    )

    LGTVControllerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SharedTransitionLayout(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(true, label = "found devices transition") { _ ->
                    HasDevices(
                        devices,
                        navigateToController = {},
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceListScreen(
    deviceListViewModel: DeviceListViewModel = hiltViewModel(),
    navigateToController: () -> Unit,
) {
    val devices by deviceListViewModel.uiState.collectAsState()
    val textInputState by deviceListViewModel.tvTextInputState.collectAsState()

    deviceListViewModel.onDeviceConnected {
        navigateToController()
    }

    ConnectedDeviceScaffold(
        errorFlow = deviceListViewModel.errors,
        textInputState = textInputState,
    ) {
        SharedTransitionLayout() {
            AnimatedContent(
                devices.devices.isNotEmpty(),
                label = "found devices transition"
            ) { hasDevices ->
                if (!hasDevices) {
                    NoDevices(
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                } else {
                    HasDevices(
                        devices.devices,
                        navigateToController,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
        }
    }
}

@Composable
fun NoDevices(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        with(sharedTransitionScope) {
            Text(
                text = stringResource(string.searching_devices),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = "txt_h1"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = stringResource(string.check_network),
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = "txt_h2"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Sonar(
                Modifier
                    .padding(horizontal = 32.dp)
                    .sharedElement(
                        rememberSharedContentState(key = "sonar"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
        }
    }
}

@Composable
fun HasDevices(
    devices: List<DeviceItemData>,
    navigateToController: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Row(
            Modifier
                .padding(top = 16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(sharedTransitionScope) {
                Column(Modifier.weight(1F)) {
                    Text(
                        text = stringResource(string.searching_devices),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(key = "txt_h1"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                    Text(
                        text = stringResource(string.check_network),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(key = "txt_h2"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }

                Sonar(
                    Modifier
                        .size(64.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "sonar"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
            }
        }

        devices.sortedWith { device1, device2 ->
            return@sortedWith when {
                !device1.isPoweredOn -> 1
                !device2.isPoweredOn -> -1
                device1.status.value == DeviceStatus.CONNECTED -> -1
                device2.status.value == DeviceStatus.CONNECTED -> 1
                else -> 0
            }
        }.forEach {
            DeviceItem(it, navigateToController)
        }
    }
}

@Composable
fun onlineTVColor() = Color.Green

@Composable
fun offlineTVColor() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

@Composable
fun DeviceItem(
    deviceData: DeviceItemData,
    navigateToController: () -> Unit,
) {
    val status by deviceData.status

    CButton(
        onClick = {
            if (status == DeviceStatus.DISCONNECTED) {
                deviceData.connect()
            }
            navigateToController()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                painterResource(
                    if (status != DeviceStatus.DISCONNECTED) R.drawable.baseline_connected_tv_24
                    else R.drawable.baseline_tv_24
                ),
                contentDescription = "contentDescription",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                tint = if (deviceData.isPoweredOn) onlineTVColor() else offlineTVColor(),
            )

            Column(modifier = Modifier.padding(start = 15.dp)) {
                Text(deviceData.displayName)
                val subText = if (!deviceData.isPoweredOn)
                    stringResource(string.device_list_offline)
                else if (status == DeviceStatus.DISCONNECTED)
                    stringResource(string.device_list_disconnected)
                else
                    stringResource(string.device_list_connected)
                Text(subText)
            }
        }
    }
}

data class DeviceItemData(
    val displayName: String,
    val status: State<DeviceStatus>,
    val isPoweredOn: Boolean,
    val connect: () -> Unit,
)

@Composable
fun Sonar(
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.tertiary
    val colorStart = color.copy(alpha = .9F)
    val colorEnd = color.copy(alpha = .3F)
    val brush = remember(colorStart, colorEnd) {
        Brush.radialGradient(
            colors = listOf(colorStart, colorEnd)
        )
    }

    Box(
        modifier = modifier
            .sizeIn(minWidth = 16.dp, minHeight = 16.dp)
            .aspectRatio(1F),
        contentAlignment = Alignment.Center
    ) {
        val transition = rememberInfiniteTransition()
        val animation = transition.animateFloat(
            initialValue = 0F,
            targetValue = 1F,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing)
            ),
            label = "Sonar Animation"
        )

        Ripple(animation.value, brush)
        Ripple((animation.value + .33F) % 1, brush)
        Ripple((animation.value + .66F) % 1, brush)
    }
}

@Composable
fun Ripple(animationValue: Float, brush: Brush) {
    Box(
        Modifier
            .scale(animationValue)
            .clip(shape = CircleShape)
            .fillMaxSize()
            .alpha(min(1F, (4F * (1F - animationValue)).pow(2)))
            .background(
                brush = brush
            )
    )
}