package com.github.heroslender.lgtvcontroller.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.Settings
import com.github.heroslender.lgtvcontroller.settings.DATA_STORE_FILE_NAME
import com.github.heroslender.lgtvcontroller.settings.SettingsRepository
import com.github.heroslender.lgtvcontroller.settings.SettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DependenciesModule {

    @Singleton
    @Provides
    fun proviteCoroutineScope(): CoroutineScope {
        return MainScope()
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(
        @ApplicationContext ctx: Context,
        coroutineScope: CoroutineScope,
    ): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler {
                Settings.getDefaultInstance()
            },
            scope = coroutineScope,
            produceFile = {
                ctx.dataStoreFile(DATA_STORE_FILE_NAME)
            },
            migrations = emptyList()
        )
    }

    @Provides
    fun provideSettingsRepository(
        settingsDataStore: DataStore<Settings>
    ): SettingsRepository {
        return SettingsRepository(settingsDataStore)
    }

    @Singleton
    @Provides
    fun provideDeviceManager(
        @ApplicationContext ctx: Context,
        coroutineScope: CoroutineScope,
        settingsRepository: SettingsRepository,
    ): DeviceManager {
        return DeviceManager(ctx, coroutineScope, settingsRepository)
    }
}