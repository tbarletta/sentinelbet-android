package com.sentinelbet.app.di;

import com.sentinelbet.app.data.db.SentinelBetDatabase;
import com.sentinelbet.app.data.db.SettingsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSettingsDaoFactory implements Factory<SettingsDao> {
  private final Provider<SentinelBetDatabase> dbProvider;

  public AppModule_ProvideSettingsDaoFactory(Provider<SentinelBetDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SettingsDao get() {
    return provideSettingsDao(dbProvider.get());
  }

  public static AppModule_ProvideSettingsDaoFactory create(
      Provider<SentinelBetDatabase> dbProvider) {
    return new AppModule_ProvideSettingsDaoFactory(dbProvider);
  }

  public static SettingsDao provideSettingsDao(SentinelBetDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSettingsDao(db));
  }
}
