package com.sentinelbet.app.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class BankrollViewModel_Factory implements Factory<BankrollViewModel> {
  @Override
  public BankrollViewModel get() {
    return newInstance();
  }

  public static BankrollViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BankrollViewModel newInstance() {
    return new BankrollViewModel();
  }

  private static final class InstanceHolder {
    private static final BankrollViewModel_Factory INSTANCE = new BankrollViewModel_Factory();
  }
}
