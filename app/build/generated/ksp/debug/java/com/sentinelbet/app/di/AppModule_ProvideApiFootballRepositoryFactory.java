package com.sentinelbet.app.di;

import com.sentinelbet.app.data.repository.ApiFootballRepository;
import com.sentinelbet.app.data.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideApiFootballRepositoryFactory implements Factory<ApiFootballRepository> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public AppModule_ProvideApiFootballRepositoryFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ApiFootballRepository get() {
    return provideApiFootballRepository(okHttpClientProvider.get(), settingsRepositoryProvider.get());
  }

  public static AppModule_ProvideApiFootballRepositoryFactory create(
      Provider<OkHttpClient> okHttpClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new AppModule_ProvideApiFootballRepositoryFactory(okHttpClientProvider, settingsRepositoryProvider);
  }

  public static ApiFootballRepository provideApiFootballRepository(OkHttpClient okHttpClient,
      SettingsRepository settingsRepository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApiFootballRepository(okHttpClient, settingsRepository));
  }
}
