package com.github.heroslender.lgtvcontroller.settings

import android.util.Log
import androidx.datastore.core.DataStore
import com.github.heroslender.lgtvcontroller.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class SettingsRepository(
    private val settingsStore: DataStore<Settings>
) {
    val settingsFlow: Flow<Settings> = settingsStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("SettingsRepo", "Error reading sort order preferences.", exception)
                emit(Settings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateFavoriteId(newId: String) {
        settingsStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setFavoriteId(newId)
                .build()
        }
    }
}