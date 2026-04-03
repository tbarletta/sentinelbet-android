package com.sentinelbet.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
public final class SubscriptionRepository_Factory implements Factory<SubscriptionRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  public SubscriptionRepository_Factory(Provider<SupabaseClient> supabaseProvider) {
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SubscriptionRepository get() {
    return newInstance(supabaseProvider.get());
  }

  public static SubscriptionRepository_Factory create(Provider<SupabaseClient> supabaseProvider) {
    return new SubscriptionRepository_Factory(supabaseProvider);
  }

  public static SubscriptionRepository newInstance(SupabaseClient supabase) {
    return new SubscriptionRepository(supabase);
  }
}
