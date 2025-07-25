package com.github.heroslender.lgtvcontroller.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.bumptech.glide.load.model.GlideUrl
import com.connectsdk.service.capability.KeyControl
import com.connectsdk.service.capability.PowerControl
import com.connectsdk.service.capability.VolumeControl
import com.github.heroslender.lgtvcontroller.ControllerTopAppBar
import com.github.heroslender.lgtvcontroller.R
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.TopAppBarAction
import com.github.heroslender.lgtvcontroller.device.Device
import com.github.heroslender.lgtvcontroller.device.DeviceStatus
import com.github.heroslender.lgtvcontroller.domain.model.App
import com.github.heroslender.lgtvcontroller.domain.model.Input
import com.github.heroslender.lgtvcontroller.ui.controller.ButtonShape
import com.github.heroslender.lgtvcontroller.ui.controller.CIconButton
import com.github.heroslender.lgtvcontroller.ui.controller.CTextButton
import com.github.heroslender.lgtvcontroller.ui.controller.PreviewDevice
import com.github.heroslender.lgtvcontroller.ui.controller.hasCapability
import com.github.heroslender.lgtvcontroller.ui.icons.TvRemote
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview()
@Composable
fun HomePreview(
    isConnected: Boolean = true,
    isFavorite: Boolean = false,
) {
    val device = if (isConnected) PreviewDevice else null
    val uiState = HomeUiState(
        device = device,
        deviceName = device?.friendlyName,
        deviceStatus = device?.let { runBlocking { it.status.first() } }
            ?: DeviceStatus.DISCONNECTED,
        runningApp = "netflix",
        isFavorite = isFavorite,
        apps = listOf(
            App(id = "youtube", name = "YouTube Yoyu YOuY asd", icon = "a"),
            App(id = "netflix", name = "Netflix", icon = "a"),
        ),
        inputs = listOf(
            Input(id = "hdmi1", name = "HDMI1", icon = "a"),
            Input(id = "hdmi2", name = "HDMI2", icon = "a", connected = true),
            Input(id = "scart", name = "SCART", icon = "a"),
        )
    )

    LGTVControllerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ControllerTopAppBar(
                    title = uiState.deviceName ?: "",
                    subtitle = buildAnnotatedString {
                        if (uiState.deviceStatus == DeviceStatus.CONNECTED) {
                            withStyle(style = SpanStyle(color = colorResource(R.color.connected))) {
                                append("● ")
                            }
                        }

                        append(stringResource(uiState.deviceStatus.nameResId))
                    },
                    titleHorizontalAlignment = Alignment.CenterHorizontally,
                    navigateUp = { },
                    actions = {
                        TopAppBarAction(
                            imageVector = if (uiState.isFavorite) Filled.Star else Filled.StarBorder,
                            contentDescription = stringResource(string.favorite_button),
                        ) { }

                        TopAppBarAction(
                            imageVector = Filled.Settings,
                            contentDescription = stringResource(string.edit_button),
                            enabled = uiState.device != null,
                        ) { }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ControllerShortcutsCard(uiState.device, {})

                AppsCard(uiState.apps, uiState.runningApp, {})

                InputsCard(uiState.inputs, uiState.runningApp, {})
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToDeviceList: () -> Unit,
    navigateToController: () -> Unit,
    navigateToEditDevice: (String) -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ControllerTopAppBar(
                title = uiState.deviceName ?: "",
                subtitle = buildAnnotatedString {
                    if (uiState.deviceStatus == DeviceStatus.CONNECTED) {
                        withStyle(style = SpanStyle(color = colorResource(R.color.connected))) {
                            append("● ")
                        }
                    }

                    append(stringResource(uiState.deviceStatus.nameResId))
                },
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                navigateUp = navigateToDeviceList,
                actions = {
                    TopAppBarAction(
                        imageVector = if (uiState.isFavorite) Filled.Star else Filled.StarBorder,
                        contentDescription = stringResource(string.favorite_button),
                    ) {
                        homeViewModel.setFavorite(!uiState.isFavorite)
                    }

                    TopAppBarAction(
                        imageVector = Filled.Settings,
                        contentDescription = stringResource(string.edit_button),
                        enabled = uiState.device != null,
                    ) {
                        uiState.device?.id?.also { navigateToEditDevice(it) }
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ControllerShortcutsCard(
                device = uiState.device,
                navigateToController = navigateToController,
            )

            AppsCard(
                apps = uiState.apps,
                runningApp = uiState.runningApp,
                openApp = { app ->
                    uiState.device?.launchApp(app.id)
                }
            )

            InputsCard(
                inputs = uiState.inputs,
                runningApp = uiState.runningApp,
                switchInput = { input ->
                    uiState.device?.launchApp(input.id)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ControllerShortcutsCard(
    device: Device?,
    navigateToController: () -> Unit,
) {
    ContentCard(
        iconVector = TvRemote,
        header = stringResource(string.controller_card_title),
        openCard = if (device != null) navigateToController else null
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = stringResource(string.mute_button),
                    enabled = device.hasCapability(VolumeControl.Mute_Set),
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F, true),
                ) {
                    device?.mute()
                }
                CIconButton(
                    iconId = R.drawable.baseline_home_24,
                    contentDescription = stringResource(string.home_button),
                    enabled = device.hasCapability(KeyControl.Home),
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F, true),
                ) {
                    device?.home()
                }
            }

            VolumeControls(device)
        }
    }
}

@Composable
fun ColumnScope.VolumeControls(
    device: Device?,
    modifier: Modifier = Modifier,
) {
    val isEnabled = device.hasCapability(VolumeControl.Volume_Up_Down)
    HorizontalControls(
        centerText = stringResource(string.volume_controls),
        modifier = modifier,
        enabled = isEnabled,
        topButton = {
            CTextButton(
                text = stringResource(string.volume_down_button),
                fontSize = 6.em,
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .aspectRatio(1.4F, false),
            ) {
                device?.volumeDown()
            }
        },
        bottomButton = {
            CTextButton(
                text = stringResource(string.volume_up_button),
                fontSize = 6.em,
                enabled = isEnabled,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .aspectRatio(1.4F, false),
            ) {
                device?.volumeUp()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun AppsCard(
    apps: List<App>,
    runningApp: String,
    openApp: (App) -> Unit,
) {
    ScrollContentCard(
        iconVector = Filled.Widgets,
        header = stringResource(string.apps_card_title),
    ) {
        for (appInfo in apps) {
            TextButton(
                onClick = { openApp(appInfo) },
                shape = IconButtonDefaults.smallSquareShape,
                modifier = Modifier
                    .height(96.dp)
                    .width(IntrinsicSize.Min),
                colors = if (appInfo.id == runningApp) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                contentPadding = PaddingValues(6.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .height(96.dp)
                ) {
                    GlideSubcomposition(
                        model = CustomGlideUrl(appInfo.icon, appInfo.name),
                        modifier = Modifier
                            .weight(1F)
                            .aspectRatio(1F, true)
                    ) {
                        when (state) {
                            RequestState.Loading -> CircularProgressIndicator(
                                color = if (appInfo.id == runningApp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                            )

                            is RequestState.Success -> Image(
                                painter = painter,
                                contentDescription = appInfo.name,
                                modifier = Modifier
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            RequestState.Failure -> CircularProgressIndicator(
                                color = if (appInfo.id == runningApp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                            )
                        }
                    }

                    Text(
                        text = appInfo.name,
                        modifier = Modifier,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = LocalTextStyle.current.fontSize * .8,
                            maxFontSize = LocalTextStyle.current.fontSize
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

class CustomGlideUrl(
    url: String,
    val cacheName: String,
) : GlideUrl(url.ifEmpty { "Not Found" }) {
    override fun getCacheKey(): String? {
        return cacheName
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun InputsCard(
    inputs: List<Input>,
    runningApp: String,
    switchInput: (Input) -> Unit,
) {
    ScrollContentCard(
        iconVector = Filled.ElectricalServices,
        header = stringResource(string.inputs_card_title),
    ) {
        for (appInfo in inputs) {
            TextButton(
                onClick = { switchInput(appInfo) },
                shape = IconButtonDefaults.smallSquareShape,
                modifier = Modifier.height(96.dp),
                colors = if (appInfo.id == runningApp) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                contentPadding = PaddingValues(6.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .height(96.dp)
                ) {
                    GlideSubcomposition(
                        model = CustomGlideUrl(appInfo.icon, appInfo.name),
                        modifier = Modifier
                            .weight(1F)
                            .aspectRatio(1F, true)
                    ) {
                        when (state) {
                            RequestState.Loading -> CircularProgressIndicator(
                                color = if (appInfo.id == runningApp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                            )

                            is RequestState.Success -> Icon(
                                painter = painter,
                                contentDescription = appInfo.name,
                                modifier = Modifier
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                            )

                            RequestState.Failure -> CircularProgressIndicator(
                                color = if (appInfo.id == runningApp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1F)
                                    .aspectRatio(1F, true)
                            )
                        }
                    }

                    Text(
                        text = buildAnnotatedString {
                            if (appInfo.connected) {
                                withStyle(style = SpanStyle(color = colorResource(R.color.connected))) {
                                    append("● ")
                                }
                            }

                            append(appInfo.name)
                        },
                        modifier = Modifier,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 8.sp,
                            maxFontSize = LocalTextStyle.current.fontSize
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollContentCard(
    iconVector: ImageVector,
    header: String,
    openCard: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    ContentCard(
        iconVector = iconVector,
        header = header,
        openCard = openCard
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            content = content
        )
    }
}

@Composable
fun ContentCard(
    iconVector: ImageVector,
    header: String,
    openCard: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = header,
                )
                Text(
                    text = header,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                if (openCard != null) {
                    Spacer(Modifier.weight(1F))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = content
            )
        }
    }

    if (openCard != null) {
        ElevatedCard(onClick = openCard, content = cardContent)
    } else {
        ElevatedCard(content = cardContent)
    }
}

@Composable
fun ColumnScope.HorizontalControls(
    centerText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    topButton: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    Surface(
        shape = ButtonShape,
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row {
            topButton()

            Text(
                text = centerText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1F),
                fontSize = 3.em,
            )

            bottomButton()
        }
    }
}
