package com.github.heroslender.lgtvcontroller.controller

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.heroslender.lgtvcontroller.R
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
    isConnected: Boolean = false,
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
                deviceStatus = controllerUiState.deviceStatus.name,
                isFavorite = controllerUiState.isFavorite,
                onFavouriteToggle = { },
                navigateToDeviceList = { },
            )

            Controls(controllerUiState.device)
        }
    }
}

@Composable
fun ControllerScreen(
    controllerViewModel: ControllerViewModel = hiltViewModel(),
    navigateToDeviceList: () -> Unit,
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
            deviceStatus = controllerUiState.deviceStatus.name,
            isFavorite = controllerUiState.isFavorite,
            onFavouriteToggle = { controllerViewModel.setFavorite(!controllerUiState.isFavorite) },
            navigateToDeviceList = navigateToDeviceList,
        )

        Controls(controllerUiState.device)
    }
}

@Composable
fun Header(
    deviceName: String?,
    deviceStatus: String,
    isFavorite: Boolean,
    onFavouriteToggle: () -> Unit,
    navigateToDeviceList: () -> Unit,
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
                Text("Device")
                Text("Disconnected")
            } else {
                Text(deviceName)
                Text(deviceStatus)
            }
        }

        if (deviceName != null) {
            Icon(
                painterResource(if (isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24),
                "Favorite",
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(0, 0) { placeable.place(0, 0) }
                    }
                    .fillMaxHeight()
                    .aspectRatio(1F, true)
                    .padding(10.dp)
                    .clickable(onClick = onFavouriteToggle)
            )
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
                R.drawable.baseline_power_24,
                "Power",
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F, true)
            ) {
                device?.powerOff()
            }

            CTextButton(
                "INFO",
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F, true)
            ) {
                device?.info()
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
                R.drawable.baseline_home_24,
                "Home",
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(bottom = 10.dp),
            ) {
                device?.home()
            }

            CIconButton(
                R.drawable.baseline_keyboard_arrow_up_24,
                "Up",
                shape = RoundedCornerShape(4.0.dp, 4.0.dp, 0.0.dp, 0.0.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.up()
            }

            CIconButton(
                R.drawable.baseline_input_24, "Source",
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
                R.drawable.baseline_keyboard_arrow_left_24,
                "Left",
                shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.left()
            }

            CTextButton(
                "OK",
                shape = CutCornerShape(0.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.ok()
            }

            CIconButton(
                R.drawable.baseline_keyboard_arrow_right_24, "Right",
                shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
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
                R.drawable.baseline_arrow_back_24,
                "Back",
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F)
                    .padding(top = 10.dp),
            ) {
                device?.back()
            }

            CIconButton(
                R.drawable.baseline_keyboard_arrow_down_24,
                "Down",
                shape = RoundedCornerShape(0.dp, 0.dp, 4.dp, 4.dp),
                modifier = Modifier
                    .aspectRatio(1F, true)
                    .weight(1F),
            ) {
                device?.down()
            }

            CIconButton(
                R.drawable.netflix,
                "Netflix",
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
    VerticalControls(
        centerText = "VOL",
        topButton = {
            CTextButton(
                "+",
                fontSize = 6.em,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.volumeUp()
            }
        },
        bottomButton = {
            CTextButton(
                "-",
                fontSize = 5.em,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.volumeDown()
            }
        }
    )
}

@Composable
fun RowScope.ChannelControls(device: Device?) {
    VerticalControls(
        centerText = "CH",
        topButton = {
            CIconButton(
                R.drawable.baseline_keyboard_arrow_up_24,
                "Channel Up",
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.channelUp()
            }
        },
        bottomButton = {
            CIconButton(
                R.drawable.baseline_keyboard_arrow_down_24,
                "Channel Down",
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {
                device?.channelDown()
            }
        }
    )
}

object PreviewDevice : Device {
    override val id: String
        get() = "asddgres--sdfsdf-sdf"
    override val friendlyName: String
        get() = "Your Awesome TV"
    override val status: Flow<DeviceStatus>
        get() = flowOf(DeviceStatus.CONNECTED)

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
    override fun launchNetflix() {}
    override fun launchApp(appId: String) {}
    override fun disconnect() {}
    override fun updateStatus(status: DeviceStatus) {}
}