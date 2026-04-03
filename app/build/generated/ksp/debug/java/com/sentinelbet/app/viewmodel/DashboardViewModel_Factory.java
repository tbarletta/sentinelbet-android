package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.BetRepository;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<BetRepository> betRepoProvider;

  private final Provider<SettingsRepository> settingsRepoProvider;

  public DashboardViewModel_Factory(Provider<BetRepository> betRepoProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    this.betRepoProvider = betRepoProvider;
    this.settingsRepoProvider = settingsRepoProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(betRepoProvider.get(), settingsRepoProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<BetRepository> betRepoProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    return new DashboardViewModel_Factory(betRepoProvider, settingsRepoProvider);
  }

  public static DashboardViewModel newInstance(BetRepository betRepo,
      SettingsRepository settingsRepo) {
    return new DashboardViewModel(betRepo, settingsRepo);
  }
}
