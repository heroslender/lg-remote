package com.github.heroslender.lgtvcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var deviceManager: DeviceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LGTVControllerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ControllerApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        deviceManager.resume()
    }

    override fun onPause() {
        super.onPause()

        deviceManager.pause()
    }
}

