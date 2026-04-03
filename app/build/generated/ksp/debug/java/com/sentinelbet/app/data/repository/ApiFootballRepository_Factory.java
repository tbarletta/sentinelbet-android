package com.sentinelbet.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ApiFootballRepository_Factory implements Factory<ApiFootballRepository> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ApiFootballRepository_Factory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ApiFootballRepository get() {
    return newInstance(okHttpClientProvider.get(), settingsRepositoryProvider.get());
  }

  public static ApiFootballRepository_Factory create(Provider<OkHttpClient> okHttpClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ApiFootballRepository_Factory(okHttpClientProvider, settingsRepositoryProvider);
  }

  public static ApiFootballRepository newInstance(OkHttpClient okHttpClient,
      SettingsRepository settingsRepository) {
    return new ApiFootballRepository(okHttpClient, settingsRepository);
  }
}
