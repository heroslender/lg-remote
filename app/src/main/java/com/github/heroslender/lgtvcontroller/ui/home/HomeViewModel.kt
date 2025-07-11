package com.github.heroslender.lgtvcontroller.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        deviceManager.connectedDevice.flatMapLatest { device ->
            if (device == null) {
                return@flatMapLatest flowOf(HomeUiState())
            }

            combine(
                device.status,
                settingsRepository.settingsFlow,
                device.apps,
                device.inputs,
                device.displayName,
            ) { deviceStatus, settings, apps, inputs, displayName ->
                HomeUiState(
                    device = device,
                    deviceName = if (displayName.isNullOrEmpty()) device.friendlyName else displayName,
                    deviceStatus = deviceStatus,
                    isFavorite = device.id == settings.favoriteId,
                    apps = apps,
                    inputs = inputs,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState())

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFavoriteId(
                if (isFavorite) deviceManager.connectedDevice.value?.id ?: "" else ""
            )
        }
    }
}