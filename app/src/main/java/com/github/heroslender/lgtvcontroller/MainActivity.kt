package com.github.heroslender.lgtvcontroller

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.github.heroslender.lgtvcontroller.controller.CIconButton
import com.github.heroslender.lgtvcontroller.controller.CTextButton
import com.github.heroslender.lgtvcontroller.controller.VerticalControls
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import com.github.heroslender.lgtvcontroller.utils.sendSpecialKey


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RemoteUtil(applicationContext.getSharedPreferences("MY_PREFS", 0)).discover(this)
        setContent {
            LGTVControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Controls()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPreview() {
    RemoteUtil.instance = object : RemoteUtil(object : SharedPreferences {
        override fun getAll(): MutableMap<String, *> {
            return mutableMapOf<String, Any>()
        }

        override fun getString(p0: String?, p1: String?): String? {
            return p1
        }

        override fun getStringSet(p0: String?, p1: MutableSet<String>?): MutableSet<String>? {
            return mutableSetOf()
        }

        override fun getInt(p0: String?, p1: Int): Int {
            return 0
        }

        override fun getLong(p0: String?, p1: Long): Long {
            return 0
        }

        override fun getFloat(p0: String?, p1: Float): Float {
            return 0F
        }

        override fun getBoolean(p0: String?, p1: Boolean): Boolean {
            return false
        }

        override fun contains(p0: String?): Boolean {
            return true
        }

        override fun edit(): SharedPreferences.Editor {
            return object : SharedPreferences.Editor {
                override fun putString(p0: String?, p1: String?): SharedPreferences.Editor {
                    return this
                }

                override fun putStringSet(
                    p0: String?,
                    p1: MutableSet<String>?
                ): SharedPreferences.Editor {
                    return this
                }

                override fun putInt(p0: String?, p1: Int): SharedPreferences.Editor {
                    return this
                }

                override fun putLong(p0: String?, p1: Long): SharedPreferences.Editor {
                    return this
                }

                override fun putFloat(p0: String?, p1: Float): SharedPreferences.Editor {
                    return this
                }

                override fun putBoolean(p0: String?, p1: Boolean): SharedPreferences.Editor {
                    return this
                }

                override fun remove(p0: String?): SharedPreferences.Editor {
                    return this
                }

                override fun clear(): SharedPreferences.Editor {
                    return this
                }

                override fun commit(): Boolean {
                    return true
                }

                override fun apply() {
                }

            }
        }

        override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        }

        override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        }
    }) {}
    LGTVControllerTheme {
        Controls()
    }
}

@Composable
fun Header() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        val device by RemoteUtil.instance.connectedDevice
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable {
                    RemoteUtil.instance.listDevices()
                }
        ) {
            if (device == null) {
                Text("Device")
                Text("Disconnected")
            } else {
                val deviceStatus by device!!.status
                Text(device!!.device.friendlyName)
                Text(deviceStatus.name)
            }
        }

        if (device != null) {
            var isFavorite by remember { mutableStateOf(RemoteUtil.instance.favorite == device?.device?.id) }
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
                    .clickable {
                        if (isFavorite) {
                            RemoteUtil.instance.favorite = ""
                        } else {
                            val deviceId = device?.device?.id
                            RemoteUtil.instance.favorite = deviceId ?: ""
                            if (deviceId == null) {
                                return@clickable
                            }
                        }

                        isFavorite = !isFavorite
                    }
            )
        }
    }
}

@Composable
fun Controls() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Header()

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {

                VolumeControls()

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
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.powerOff()
                    }
                    CTextButton(
                        "INFO",
                        modifier = Modifier
                            .weight(1F)
                            .aspectRatio(1F, true)
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CTextButton
                        device.service.sendSpecialKey("INFO")
                    }
                }

                ChannelControls()
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
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.home(null)
                    }

                    CIconButton(
                        R.drawable.baseline_keyboard_arrow_up_24,
                        "Up",
                        shape = RoundedCornerShape(4.0.dp, 4.0.dp, 0.0.dp, 0.0.dp),
                        modifier = Modifier
                            .aspectRatio(1F, true)
                            .weight(1F),
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.keyControl.up(null)
                    }

                    CIconButton(
                        R.drawable.baseline_input_24, "Source",
                        modifier = Modifier
                            .aspectRatio(1F, true)
                            .weight(1F)
                            .padding(bottom = 10.dp),
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.getExternalInputList(null)
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
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.keyControl.left(null)
                    }

                    CTextButton(
                        "OK",
                        shape = CutCornerShape(0.dp),
                        modifier = Modifier
                            .aspectRatio(1F, true)
                            .weight(1F),
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CTextButton
                        device.service.keyControl.ok(null)
                    }

                    CIconButton(
                        R.drawable.baseline_keyboard_arrow_right_24, "Right",
                        shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
                        modifier = Modifier
                            .aspectRatio(1F, true)
                            .weight(1F),
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.keyControl.right(null)
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
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.keyControl.back(null)
                    }

                    CIconButton(
                        R.drawable.baseline_keyboard_arrow_down_24,
                        "Down",
                        shape = RoundedCornerShape(0.dp, 0.dp, 4.dp, 4.dp),
                        modifier = Modifier
                            .aspectRatio(1F, true)
                            .weight(1F),
                    ) {
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.keyControl.down(null)
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
                        val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                        device.service.launchApp("netflix", null)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.VolumeControls() {
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
                val device = RemoteUtil.instance.connectedDevice.value ?: return@CTextButton
                device.service.volumeUp()
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
                val device = RemoteUtil.instance.connectedDevice.value ?: return@CTextButton
                device.service.volumeDown()
            }
        }
    )
}

@Composable
fun RowScope.ChannelControls() {
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
                val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                device.service.channelUp()
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
                val device = RemoteUtil.instance.connectedDevice.value ?: return@CIconButton
                device.service.channelDown()
            }
        }
    )
}