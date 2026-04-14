package com.sentinelbet.app.data.repository;

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
public final class ApiKeyRepository_Factory implements Factory<ApiKeyRepository> {
  private final Provider<SecurePreferences> securePrefsProvider;

  public ApiKeyRepository_Factory(Provider<SecurePreferences> securePrefsProvider) {
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public ApiKeyRepository get() {
    return newInstance(securePrefsProvider.get());
  }

  public static ApiKeyRepository_Factory create(Provider<SecurePreferences> securePrefsProvider) {
    return new ApiKeyRepository_Factory(securePrefsProvider);
  }

  public static ApiKeyRepository newInstance(SecurePreferences securePrefs) {
    return new ApiKeyRepository(securePrefs);
  }
}
