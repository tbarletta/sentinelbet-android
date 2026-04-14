package com.sentinelbet.app.data.repository;

import com.sentinelbet.app.data.db.SettingsDao;
import com.sentinelbet.app.data.secure.SecurePreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SettingsRepository_Factory implements Factory<SettingsRepository> {
  private final Provider<SettingsDao> daoProvider;

  private final Provider<SecurePreferences> securePrefsProvider;

  public SettingsRepository_Factory(Provider<SettingsDao> daoProvider,
      Provider<SecurePreferences> securePrefsProvider) {
    this.daoProvider = daoProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public SettingsRepository get() {
    return newInstance(daoProvider.get(), securePrefsProvider.get());
  }

  public static SettingsRepository_Factory create(Provider<SettingsDao> daoProvider,
      Provider<SecurePreferences> securePrefsProvider) {
    return new SettingsRepository_Factory(daoProvider, securePrefsProvider);
  }

  public static SettingsRepository newInstance(SettingsDao dao, SecurePreferences securePrefs) {
    return new SettingsRepository(dao, securePrefs);
  }
}
