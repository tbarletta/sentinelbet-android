package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.ApiFootballRepository;
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
public final class FixturesViewModel_Factory implements Factory<FixturesViewModel> {
  private final Provider<ApiFootballRepository> repoProvider;

  private final Provider<SettingsRepository> settingsRepoProvider;

  public FixturesViewModel_Factory(Provider<ApiFootballRepository> repoProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    this.repoProvider = repoProvider;
    this.settingsRepoProvider = settingsRepoProvider;
  }

  @Override
  public FixturesViewModel get() {
    return newInstance(repoProvider.get(), settingsRepoProvider.get());
  }

  public static FixturesViewModel_Factory create(Provider<ApiFootballRepository> repoProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    return new FixturesViewModel_Factory(repoProvider, settingsRepoProvider);
  }

  public static FixturesViewModel newInstance(ApiFootballRepository repo,
      SettingsRepository settingsRepo) {
    return new FixturesViewModel(repo, settingsRepo);
  }
}
