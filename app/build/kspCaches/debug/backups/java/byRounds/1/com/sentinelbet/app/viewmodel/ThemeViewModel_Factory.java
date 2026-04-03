package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ThemeViewModel_Factory implements Factory<ThemeViewModel> {
  private final Provider<SettingsRepository> settingsProvider;

  public ThemeViewModel_Factory(Provider<SettingsRepository> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public ThemeViewModel get() {
    return newInstance(settingsProvider.get());
  }

  public static ThemeViewModel_Factory create(Provider<SettingsRepository> settingsProvider) {
    return new ThemeViewModel_Factory(settingsProvider);
  }

  public static ThemeViewModel newInstance(SettingsRepository settings) {
    return new ThemeViewModel(settings);
  }
}
