package com.sentinelbet.app.viewmodel;

import com.sentinelbet.app.data.repository.AuthRepository;
import com.sentinelbet.app.data.repository.SubscriptionRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SubscriptionRepository> subscriptionRepositoryProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.subscriptionRepositoryProvider = subscriptionRepositoryProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), subscriptionRepositoryProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, subscriptionRepositoryProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      SubscriptionRepository subscriptionRepository) {
    return new AuthViewModel(authRepository, subscriptionRepository);
  }
}
