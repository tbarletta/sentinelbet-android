package com.sentinelbet.app.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<Context> contextProvider;

  private final Provider<SubscriptionRepository> subscriptionRepositoryProvider;

  public AuthRepository_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<Context> contextProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    this.supabaseProvider = supabaseProvider;
    this.contextProvider = contextProvider;
    this.subscriptionRepositoryProvider = subscriptionRepositoryProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(supabaseProvider.get(), contextProvider.get(), subscriptionRepositoryProvider.get());
  }

  public static AuthRepository_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<Context> contextProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    return new AuthRepository_Factory(supabaseProvider, contextProvider, subscriptionRepositoryProvider);
  }

  public static AuthRepository newInstance(SupabaseClient supabase, Context context,
      SubscriptionRepository subscriptionRepository) {
    return new AuthRepository(supabase, context, subscriptionRepository);
  }
}
