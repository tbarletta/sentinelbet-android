package com.sentinelbet.app.di

import android.content.Context
import androidx.room.Room
import com.sentinelbet.app.data.db.*
import com.sentinelbet.app.data.repository.ApiFootballRepository
import com.sentinelbet.app.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): SentinelBetDatabase =
        Room.databaseBuilder(ctx, SentinelBetDatabase::class.java, "sentinelbet.db")
            .addCallback(DatabaseSeedCallback())
            .build()

    @Provides
    fun provideBetDao(db: SentinelBetDatabase): BetDao = db.betDao()

    @Provides
    fun provideSettingsDao(db: SentinelBetDatabase): SettingsDao = db.settingsDao()

    @Provides
    @Singleton
    fun provideApiFootballRepository(
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository,
    ): ApiFootballRepository = ApiFootballRepository(okHttpClient, settingsRepository)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)
            .build()
}
