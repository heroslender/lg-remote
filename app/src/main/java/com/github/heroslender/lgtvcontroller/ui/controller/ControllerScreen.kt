package com.github.heroslender.lgtvcontroller.ui.controller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
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
import androidx.compose.material.icons.filled.KeyboardAlt
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.heroslender.lgtvcontroller.ControllerTopAppBar
import com.github.heroslender.lgtvcontroller.R
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.device.DeviceControllerButton
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.device.impl.PreviewDevice
import com.github.heroslender.lgtvcontroller.ui.ConnectedDeviceScaffold
import com.github.heroslender.lgtvcontroller.ui.TvTextInputState
import com.github.heroslender.lgtvcontroller.ui.icons.MyIconPack
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.TrackpadInput
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun ControlPreview() {
    ControllerScreenPreview(isConnected = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ControlPreviewDisconnected() {
    ControllerScreenPreview(isConnected = false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreenPreview(
    isConnected: Boolean,
) {
    val device = if (isConnected) PreviewDevice else null
    val controllerUiState = ControllerUiState(
        deviceName = device?.friendlyName,
        deviceStatus = device?.let { runBlocking { it.state.first().status } }
            ?: DeviceStatus.DISCONNECTED,
        hasCapability = { true },
        executeButton = {},
    )

    ControllerScreen(
        controllerUiState = controllerUiState,
        tvTextInputState = TvTextInputState(),
        errorFlow = emptyFlow(),
        navigateUp = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
) {
    val controllerUiState by controllerViewModel.uiState.collectAsState()
    val tvTextInputState by controllerViewModel.tvTextInputState.collectAsState()

    ControllerScreen(
        controllerUiState,
        tvTextInputState,
        controllerViewModel.errors,
        navigateUp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(
    controllerUiState: ControllerUiState,
    tvTextInputState: TvTextInputState,
    errorFlow: Flow<Snackbar>,
    navigateUp: () -> Unit,
) {
    ConnectedDeviceScaffold(
        errorFlow = errorFlow,
        textInputState = tvTextInputState,
        topBar = {
            ControllerTopAppBar(
                title = stringResource(string.controller_title),
                navigateUp = navigateUp,
            )
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(ControlsSpacing),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            var trackpadEnabled by remember { mutableStateOf(false) }
            Header(
                deviceName = controllerUiState.deviceName,
                deviceStatus = controllerUiState.deviceStatus,
                trackpadEnabled = trackpadEnabled,
                setTrackpadEnabled = { trackpadEnabled = it },
                hasPowerCapability = controllerUiState.hasCapability(DeviceControllerButton.POWER),
                powerOff = { controllerUiState.executeButton(DeviceControllerButton.POWER) },
                navigateToDeviceList = navigateUp,
            )

            Controls(
                trackpadEnabled = trackpadEnabled,
                clickMouse = controllerUiState.clickMouse,
                moveMouse = controllerUiState.moveMouse,
                scroll = controllerUiState.scroll,
                hasCapability = controllerUiState.hasCapability,
                executeButton = controllerUiState.executeButton,
            )
        }
    }
}

@Composable
fun Header(
    deviceName: String?,
    deviceStatus: DeviceStatus,
    trackpadEnabled: Boolean,
    setTrackpadEnabled: (Boolean) -> Unit,
    hasPowerCapability: Boolean,
    powerOff: () -> Unit,
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
                PowerButton(hasPowerCapability, powerOff)
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

            Row(horizontalArrangement = Arrangement.Absolute.Right) {
                FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    onClick = {
                        setTrackpadEnabled(!trackpadEnabled)
                    },
                ) {
                    Icon(
                        imageVector = if (trackpadEnabled) Icons.Filled.KeyboardAlt else MyIconPack.TrackpadInput,
                        contentDescription = stringResource(string.power_button),
                    )
                }
            }
        }
    }
}

@Composable
fun PowerButton(
    hasPowerCapability: Boolean,
    powerOff: () -> Unit,
) {
    if (hasPowerCapability) {
        FilledIconButton(
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.power)
            ),
            onClick = {
                powerOff()
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
fun ColumnScope.Controls(
    trackpadEnabled: Boolean,
    clickMouse: () -> Unit,
    moveMouse: (Double, Double) -> Unit,
    scroll: (Double, Double) -> Unit,
    hasCapability: (DeviceControllerButton) -> Boolean,
    executeButton: (DeviceControllerButton) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlsSpacing),
            modifier = Modifier
                .padding(bottom = ControlsSpacing)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
            VolumeControls(
                hasCapability = hasCapability,
                executeButton = executeButton,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
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
                    enabled = hasCapability(DeviceControllerButton.SOURCE),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.SOURCE)
                }

                CIconButton(
                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = stringResource(string.mute_button),
                    enabled = hasCapability(DeviceControllerButton.MUTE),
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F, true),
                ) {
                    executeButton(DeviceControllerButton.MUTE)
                }
            }

            ChannelControls(
                hasCapability = hasCapability,
                executeButton = executeButton,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
            )
        }

        if (trackpadEnabled) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(bottom = 8.dp)
                    .draggable2D(
                        state = rememberDraggable2DState { delta ->
                            moveMouse(delta.x.toDouble(), delta.y.toDouble())
                        },
                    )
                    .clickable(onClick = {
                        clickMouse()
                    })
            ) {}

        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ControlsSpacing),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                CIconButton(
                    iconId = R.drawable.baseline_home_24,
                    contentDescription = stringResource(string.home_button),
                    enabled = hasCapability(DeviceControllerButton.HOME),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F)
                        .padding(bottom = ControlsSpacing),
                ) {
                    executeButton(DeviceControllerButton.HOME)
                }

                CIconButton(
                    iconId = R.drawable.baseline_keyboard_arrow_up_24,
                    contentDescription = stringResource(string.up_button),
                    enabled = hasCapability(DeviceControllerButton.UP),
                    shape = ButtonShape.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    ),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.UP)
                }

                CIconButton(
                    iconId = R.drawable.baseline_settings_24,
                    contentDescription = stringResource(string.settings_button),
                    enabled = hasCapability(DeviceControllerButton.QMENU),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F)
                        .padding(bottom = ControlsSpacing),
                ) {
                    executeButton(DeviceControllerButton.QMENU)
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
                    enabled = hasCapability(DeviceControllerButton.LEFT),
                    shape = ButtonShape.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    ),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.LEFT)
                }

                CTextButton(
                    text = stringResource(string.ok_button),
                    shape = CutCornerShape(0.dp),
                    enabled = hasCapability(DeviceControllerButton.OK),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.OK)
                }

                CIconButton(
                    iconId = R.drawable.baseline_keyboard_arrow_right_24,
                    contentDescription = stringResource(string.right_button),
                    shape = ButtonShape.copy(
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    ),
                    enabled = hasCapability(DeviceControllerButton.RIGHT),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.RIGHT)
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
                    enabled = hasCapability(DeviceControllerButton.BACK),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F)
                        .padding(top = ControlsSpacing),
                ) {
                    executeButton(DeviceControllerButton.BACK)
                }

                CIconButton(
                    iconId = R.drawable.baseline_keyboard_arrow_down_24,
                    contentDescription = stringResource(string.down_button),
                    shape = ButtonShape.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp)
                    ),
                    enabled = hasCapability(DeviceControllerButton.DOWN),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F),
                ) {
                    executeButton(DeviceControllerButton.DOWN)
                }

                CTextButton(
                    text = "Exit",
                    enabled = hasCapability(DeviceControllerButton.EXIT),
                    modifier = Modifier
                        .aspectRatio(1F, true)
                        .weight(1F)
                        .padding(top = ControlsSpacing),
                ) {
                    executeButton(DeviceControllerButton.EXIT)
                }
            }
        }
    }
}

@Composable
fun RowScope.VolumeControls(
    hasCapability: (DeviceControllerButton) -> Boolean,
    executeButton: (DeviceControllerButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEnabled = hasCapability(DeviceControllerButton.VOLUME_UP)
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
                executeButton(DeviceControllerButton.VOLUME_UP)
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
                executeButton(DeviceControllerButton.VOLUME_DOWN)
            }
        }
    )
}

@Composable
fun RowScope.ChannelControls(
    hasCapability: (DeviceControllerButton) -> Boolean,
    executeButton: (DeviceControllerButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEnabled = hasCapability(DeviceControllerButton.CHANNEL_UP)
    VerticalControls(
        centerText = stringResource(string.channel_controls),
        modifier = modifier,
        enabled = isEnabled,
        topButton = {
            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_up_24,
                contentDescription = stringResource(string.channel_up_button),
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                executeButton(DeviceControllerButton.CHANNEL_UP)
            }
        },
        bottomButton = {
            CIconButton(
                iconId = R.drawable.baseline_keyboard_arrow_down_24,
                contentDescription = stringResource(string.channel_down_button),
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                executeButton(DeviceControllerButton.CHANNEL_DOWN)
            }
        }
    )
}

