package com.sentinelbet.app.data.repository;

import com.sentinelbet.app.data.db.BetDao;
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
public final class BetRepository_Factory implements Factory<BetRepository> {
  private final Provider<BetDao> daoProvider;

  public BetRepository_Factory(Provider<BetDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public BetRepository get() {
    return newInstance(daoProvider.get());
  }

  public static BetRepository_Factory create(Provider<BetDao> daoProvider) {
    return new BetRepository_Factory(daoProvider);
  }

  public static BetRepository newInstance(BetDao dao) {
    return new BetRepository(dao);
  }
}
