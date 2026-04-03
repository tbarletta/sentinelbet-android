package com.sentinelbet.app.domain.usecases;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ComputeAnalysisUseCase_Factory implements Factory<ComputeAnalysisUseCase> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public ComputeAnalysisUseCase_Factory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public ComputeAnalysisUseCase get() {
    return newInstance(okHttpClientProvider.get());
  }

  public static ComputeAnalysisUseCase_Factory create(Provider<OkHttpClient> okHttpClientProvider) {
    return new ComputeAnalysisUseCase_Factory(okHttpClientProvider);
  }

  public static ComputeAnalysisUseCase newInstance(OkHttpClient okHttpClient) {
    return new ComputeAnalysisUseCase(okHttpClient);
  }
}
