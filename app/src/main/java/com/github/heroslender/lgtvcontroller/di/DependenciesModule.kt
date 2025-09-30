package com.github.heroslender.lgtvcontroller.di

import android.content.Context
import com.github.heroslender.lgtvcontroller.DeviceManager
import com.github.heroslender.lgtvcontroller.storage.OfflineTvRepository
import com.github.heroslender.lgtvcontroller.storage.TvDatabase
import com.github.heroslender.lgtvcontroller.storage.TvRepository
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
    fun provideDeviceManager(
        @ApplicationContext ctx: Context,
        coroutineScope: CoroutineScope,
        tvRepository: TvRepository,
    ): DeviceManager {
        return DeviceManager(ctx, coroutineScope, tvRepository)
    }

    @Singleton
    @Provides
    fun provideTvDatabase(
        @ApplicationContext ctx: Context,
    ): TvDatabase {
        return TvDatabase.getDatabase(ctx)
    }

    @Provides
    fun providesTvRepository(
        tvDatabase: TvDatabase,
    ): TvRepository {
        return OfflineTvRepository(tvDatabase.tvDao())
    }
}