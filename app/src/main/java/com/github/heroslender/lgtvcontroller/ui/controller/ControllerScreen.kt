package com.github.heroslender.lgtvcontroller.ui.controller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.connectsdk.service.capability.ExternalInputControl
import com.connectsdk.service.capability.KeyControl
import com.connectsdk.service.capability.Launcher
import com.connectsdk.service.capability.PowerControl
import com.connectsdk.service.capability.TVControl
import com.connectsdk.service.capability.VolumeControl
import com.github.heroslender.lgtvcontroller.R
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

@Preview(showBackground = true)
@Composable
fun ControlPreview(
    isConnected: Boolean = true,
    isFavorite: Boolean = false,
) {
    val device = if (isConnected) PreviewDevice else null
    val controllerUiState = ControllerUiState(
        device = device,
        deviceName = device?.friendlyName,
        deviceStatus = device?.let { runBlocking { it.status.first() } }
            ?: DeviceStatus.DISCONNECTED,
        isFavorite = isFavorite
    )

    LGTVControllerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Header(
                deviceName = controllerUiState.deviceName,
                deviceStatus = controllerUiState.deviceStatus,
                isFavorite = controllerUiState.isFavorite,
                onFavouriteToggle = { },
                navigateToDeviceList = { },
                navigateToEditDevice = { },
            )

            Controls(controllerUiState.device)
        }
    }
}

@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel = hiltViewModel(),
    navigateToDeviceList: () -> Unit,
    navigateToEditDevice: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        val controllerUiState by controllerViewModel.uiState.collectAsState()
        Header(
            deviceName = controllerUiState.deviceName,
            deviceStatus = controllerUiState.deviceStatus,
            isFavorite = controllerUiState.isFavorite,
            onFavouriteToggle = { controllerViewModel.setFavorite(!controllerUiState.isFavorite) },
            navigateToDeviceList = navigateToDeviceList,
            navigateToEditDevice = { controllerUiState.device?.id?.also { navigateToEditDevice(it) } },
        )

        Controls(controllerUiState.device)
    }
}

@Composable
fun Header(
    deviceName: String?,
    deviceStatus: DeviceStatus,
    isFavorite: Boolean,
    onFavouriteToggle: () -> Unit,
    navigateToDeviceList: () -> Unit,
    navigateToEditDevice: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                navigateToDeviceList()
            }
        ) {
            if (deviceName == null) {
                Text(stringResource(string.controller_device))
                Text(stringResource(string.controller_disconnected))
            } else {
                Text(deviceName)
                Text(deviceStatus.name)
            }
        }

        if (deviceName != null && deviceStatus == DeviceStatus.CONNECTED) {
            Row(modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(16, 16) { placeable.place(0, 0) }
                }
                .fillMaxHeight()) {
                Icon(
                    painter = painterResource(if (isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24),
                    contentDescription = stringResource(string.favorite_button),
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1F, true)
                        .padding(10.dp)
                        .clickable(onClick = onFavouriteToggle)
                )
                Icon(
                    imageVector = Filled.Edit,
                    contentDescription = stringResource(string.edit_button),
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1F, true)
                        .padding(10.dp)
                        .clickable(onClick = navigateToEditDevice)
                )
            }
        }
    }
}

@Composable
fun ColumnScope.Controls(device: Device?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        VolumeControls(device)

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        ) {
            CIconButton(
                iconId = R.drawable.baseline_power_24,
                contentDescription = stringResource(string.power_button),
                enabled = device.hasCapability(PowerControl.Any),
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F, true),
            ) {
                device?.powerOff()
            }

            CIconButton(
                iconId = R.drawable.baseline_settings_24,
                contentDescription = stringResource(string.settings_button),
                enabled = device.hasCapability(KeyControl.Any),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
            ) {
                device?.qmenu()
            }
        }

        ChannelControls(device)
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            CIconButton(
                iconId = R.drawable.baseline_home_24,
                contentDescription = stringResource(string.home_button),
                enabled = device.hasCapability(KeyControl.Home),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(bottom = 10.dp),
            ) {
                device?.home()
            }

            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_up_24,
                contentDescription = stringResource(string.up_button),
                enabled = device.hasCapability(KeyControl.Up),
                shape = RoundedCornerShape(4.0.dp, 4.0.dp, 0.0.dp, 0.0.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.up()
            }

            CIconButton(
                iconId = R.drawable.baseline_input_24,
                contentDescription = stringResource(string.source_button),
                enabled = device.hasCapability(ExternalInputControl.Any),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(bottom = 10.dp),
            ) {
                device?.source()
            }
        }
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_left_24,
                contentDescription = stringResource(string.left_button),
                enabled = device.hasCapability(KeyControl.Left),
                shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.left()
            }

            CTextButton(
                text = stringResource(string.ok_button),
                shape = CutCornerShape(0.dp),
                enabled = device.hasCapability(KeyControl.OK),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.ok()
            }

            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_right_24,
                contentDescription = stringResource(string.right_button),
                shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
                enabled = device.hasCapability(KeyControl.Right),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.right()
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            CIconButton(
                iconId = R.drawable.baseline_arrow_back_24,
                contentDescription = stringResource(string.back_button),
                enabled = device.hasCapability(KeyControl.Back),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(top = 10.dp),
            ) {
                device?.back()
            }

            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_down_24,
                contentDescription = stringResource(string.down_button),
                shape = RoundedCornerShape(0.dp, 0.dp, 4.dp, 4.dp),
                enabled = device.hasCapability(KeyControl.Down),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.down()
            }

            CIconButton(
                iconId = R.drawable.netflix,
                contentDescription = stringResource(string.netflix_button),
                enabled = device.hasCapability(Launcher.Netflix),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(top = 10.dp),
                useDefaultTint = true
            ) {
                device?.launchNetflix()
            }
        }
    }
}

@Composable
fun RowScope.VolumeControls(device: Device?) {
    val isEnabled = device.hasCapability(VolumeControl.Volume_Up_Down)
    VerticalControls(
        centerText = stringResource(string.volume_controls),
        enabled = isEnabled,
        topButton = {
            CTextButton(
                text = stringResource(string.volume_up_button),
                fontSize = 6.em,
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
            ) {
                device?.volumeUp()
            }
        },
        bottomButton = {
            CTextButton(
                text = stringResource(string.volume_down_button),
                fontSize = 5.em,
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
            ) {
                device?.volumeDown()
            }
        }
    )
}

@Composable
fun RowScope.ChannelControls(device: Device?) {
    VerticalControls(
        centerText = stringResource(string.channel_controls),
        enabled =  device.hasCapability(TVControl.Channel_Up) || device.hasCapability(TVControl.Channel_Down),
        topButton = {
            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_up_24,
                contentDescription = stringResource(string.channel_up_button),
                enabled = device.hasCapability(TVControl.Channel_Up),
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.channelUp()
            }
        },
        bottomButton = {
            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_down_24,
                contentDescription = stringResource(string.channel_down_button),
                enabled = device.hasCapability(TVControl.Channel_Down),
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.channelDown()
            }
        }
    )
}

fun Device?.hasCapability(capability: String) = this?.hasCapability(capability) ?: false

object PreviewDevice : Device {
    override val id: String = "asddgres--sdfsdf-sdf"
    override val friendlyName: String = "Your Awesome TV"
    override val displayName: String? = null
    override val status: Flow<DeviceStatus> = flowOf(DeviceStatus.CONNECTED)

    override fun hasCapability(capability: String): Boolean = true
    override fun powerOff() {}
    override fun volumeUp() {}
    override fun volumeDown() {}
    override fun channelUp() {}
    override fun channelDown() {}
    override fun up() {}
    override fun down() {}
    override fun left() {}
    override fun right() {}
    override fun ok() {}
    override fun back() {}
    override fun home() {}
    override fun info() {}
    override fun source() {}
    override fun menu() {}
    override fun qmenu() {}
    override fun launchNetflix() {}
    override fun launchApp(appId: String) {}
    override fun disconnect() {}
    override fun updateStatus(status: DeviceStatus) {}
}