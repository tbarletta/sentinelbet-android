package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.BetRepository;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<BetRepository> repoProvider;

  public HistoryViewModel_Factory(Provider<BetRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<BetRepository> repoProvider) {
    return new HistoryViewModel_Factory(repoProvider);
  }

  public static HistoryViewModel newInstance(BetRepository repo) {
    return new HistoryViewModel(repo);
  }
}
