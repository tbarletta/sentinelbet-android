package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.ApiKeyRepository;
import com.sentinelbet.app.domain.usecases.ComputeAnalysisUseCase;
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
public final class AnalysisViewModel_Factory implements Factory<AnalysisViewModel> {
  private final Provider<ComputeAnalysisUseCase> useCaseProvider;

  private final Provider<ApiKeyRepository> apiKeyRepoProvider;

  public AnalysisViewModel_Factory(Provider<ComputeAnalysisUseCase> useCaseProvider,
      Provider<ApiKeyRepository> apiKeyRepoProvider) {
    this.useCaseProvider = useCaseProvider;
    this.apiKeyRepoProvider = apiKeyRepoProvider;
  }

  @Override
  public AnalysisViewModel get() {
    return newInstance(useCaseProvider.get(), apiKeyRepoProvider.get());
  }

  public static AnalysisViewModel_Factory create(Provider<ComputeAnalysisUseCase> useCaseProvider,
      Provider<ApiKeyRepository> apiKeyRepoProvider) {
    return new AnalysisViewModel_Factory(useCaseProvider, apiKeyRepoProvider);
  }

  public static AnalysisViewModel newInstance(ComputeAnalysisUseCase useCase,
      ApiKeyRepository apiKeyRepo) {
    return new AnalysisViewModel(useCase, apiKeyRepo);
  }
}
