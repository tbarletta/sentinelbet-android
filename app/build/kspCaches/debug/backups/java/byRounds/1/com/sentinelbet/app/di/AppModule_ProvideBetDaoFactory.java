package com.sentinelbet.app.di;

import com.sentinelbet.app.data.db.BetDao;
import com.sentinelbet.app.data.db.SentinelBetDatabase;
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
public final class AppModule_ProvideBetDaoFactory implements Factory<BetDao> {
  private final Provider<SentinelBetDatabase> dbProvider;

  public AppModule_ProvideBetDaoFactory(Provider<SentinelBetDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BetDao get() {
    return provideBetDao(dbProvider.get());
  }

  public static AppModule_ProvideBetDaoFactory create(Provider<SentinelBetDatabase> dbProvider) {
    return new AppModule_ProvideBetDaoFactory(dbProvider);
  }

  public static BetDao provideBetDao(SentinelBetDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBetDao(db));
  }
}
