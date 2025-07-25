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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.connectsdk.service.capability.ExternalInputControl
import com.connectsdk.service.capability.KeyControl
import com.connectsdk.service.capability.PowerControl
import com.connectsdk.service.capability.TVControl
import com.connectsdk.service.capability.VolumeControl
import com.github.heroslender.lgtvcontroller.ControllerTopAppBar
import com.github.heroslender.lgtvcontroller.R
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input
import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun ControlPreview() {
    ControllerScreenPreview(
        isConnected = true,
        isFavorite = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ControlPreviewDisconnected() {
    ControllerScreenPreview(
        isConnected = false,
        isFavorite = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreenPreview(
    isConnected: Boolean,
    isFavorite: Boolean,
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
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ControllerTopAppBar(
                    title = stringResource(R.string.controller_title),
                    navigateUp = {},
                )
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(ControlsSpacing),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 4.dp)
            ) {
                Header(
                    device = controllerUiState.device,
                    deviceName = controllerUiState.deviceName,
                    deviceStatus = controllerUiState.deviceStatus,
                    navigateToDeviceList = { },
                )

                Controls(controllerUiState.device)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ControllerTopAppBar(
                title = stringResource(R.string.controller_title),
                navigateUp = navigateUp,
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(ControlsSpacing),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            val controllerUiState by controllerViewModel.uiState.collectAsState()
            Header(
                device = controllerUiState.device,
                deviceName = controllerUiState.deviceName,
                deviceStatus = controllerUiState.deviceStatus,
                navigateToDeviceList = navigateUp,
            )

            Controls(controllerUiState.device)
        }
    }
}

@Composable
fun Header(
    device: Device?,
    deviceName: String?,
    deviceStatus: DeviceStatus,
    navigateToDeviceList: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(), onClick = {}) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.Absolute.Left) {
                PowerButton(device)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = navigateToDeviceList)
            ) {
                if (deviceName == null) {
                    Text(stringResource(string.controller_device))
                    Text(stringResource(string.controller_disconnected))
                } else {
                    Text(deviceName)
                    Text(
                        text = stringResource(deviceStatus.nameResId),
                        color = colorResource(R.color.connected)
                    )
                }
            }
        }
    }
}

@Composable
fun PowerButton(device: Device?) {
    if (device.hasCapability(PowerControl.Any)) {
        FilledIconButton(
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.power)
            ),
            onClick = {
                device?.powerOff()
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_power_24),
                contentDescription = stringResource(string.power_button),
            )
        }
    }
}

@Composable
fun ColumnScope.Controls(device: Device?) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlsSpacing),
            modifier = Modifier
                .padding(bottom = ControlsSpacing)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            VolumeControls(
                device = device,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(ControlsSpacing),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            ) {
                CIconButton(
                    iconId = R.drawable.baseline_input_24,
                    contentDescription = stringResource(string.source_button),
                    enabled = device.hasCapability(ExternalInputControl.Any),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    device?.source()
                }

                CIconButton(
                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = stringResource(string.mute_button),
                    enabled = device.hasCapability(VolumeControl.Mute_Set),
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F, true),
                ) {
                    device?.mute()
                }
            }

            ChannelControls(
                device = device,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlsSpacing),
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
                    .padding(bottom = ControlsSpacing),
            ) {
                device?.home()
            }

            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_up_24,
                contentDescription = stringResource(string.up_button),
                enabled = device.hasCapability(KeyControl.Up),
                shape = ButtonShape.copy(
                    bottomEnd = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                ),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.up()
            }

            CIconButton(
                iconId = R.drawable.baseline_settings_24,
                contentDescription = stringResource(string.settings_button),
                enabled = device.hasCapability(KeyControl.Any),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(bottom = ControlsSpacing),
            ) {
                device?.qmenu()
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
                shape = ButtonShape.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
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
                shape = ButtonShape.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                ),
                enabled = device.hasCapability(KeyControl.Right),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.right()
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlsSpacing),
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
                    .padding(top = ControlsSpacing),
            ) {
                device?.back()
            }

            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_down_24,
                contentDescription = stringResource(string.down_button),
                shape = ButtonShape.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp)),
                enabled = device.hasCapability(KeyControl.Down),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.down()
            }

            CTextButton(
                text = "Exit",
                enabled = device.hasCapability(KeyControl.Any),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(top = ControlsSpacing),
            ) {
                device?.exit()
            }
        }
    }
}

@Composable
fun RowScope.VolumeControls(
    device: Device?,
    modifier: Modifier = Modifier,
) {
    val isEnabled = device.hasCapability(VolumeControl.Volume_Up_Down)
    VerticalControls(
        centerText = stringResource(string.volume_controls),
        modifier = modifier,
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
                fontSize = 6.em,
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
fun RowScope.ChannelControls(
    device: Device?,
    modifier: Modifier = Modifier,
) {
    VerticalControls(
        centerText = stringResource(string.channel_controls),
        modifier = modifier,
        enabled = device.hasCapability(TVControl.Channel_Up) || device.hasCapability(TVControl.Channel_Down),
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

fun Device?.hasCapability(capability: String) = this?.hasCapability(capability) == true

object PreviewDevice : Device {
    override val id: String = "gads-sdgfds-g-fdsgfdgdf-sdfsdf"
    override val friendlyName: String = "Living Room TV"
    override val displayName: Flow<String?> = flowOf("Living Room TV")
    override val tv: Tv = Tv(id, friendlyName, "Living Room TV", emptyList(), emptyList())
    override val status: Flow<DeviceStatus> = flowOf(DeviceStatus.CONNECTED)
    override val apps: Flow<List<App>> = flowOf(emptyList())
    override val inputs: Flow<List<Input>> = flowOf(emptyList())
    override val runningApp: Flow<String> = flowOf("")

    override fun hasCapability(capability: String): Boolean = true
    override fun powerOff() {}
    override fun volumeUp() {}
    override fun volumeDown() {}
    override fun mute() {}
    override fun channelUp() {}
    override fun channelDown() {}
    override fun up() {}
    override fun down() {}
    override fun left() {}
    override fun right() {}
    override fun ok() {}
    override fun back() {}
    override fun exit() {}
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