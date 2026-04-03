package com.sentinelbet.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.sentinelbet.app.data.db.BetDao;
import com.sentinelbet.app.data.db.SentinelBetDatabase;
import com.sentinelbet.app.data.db.SettingsDao;
import com.sentinelbet.app.data.repository.ApiFootballRepository;
import com.sentinelbet.app.data.repository.ApiKeyRepository;
import com.sentinelbet.app.data.repository.AuthRepository;
import com.sentinelbet.app.data.repository.BetRepository;
import com.sentinelbet.app.data.repository.SettingsRepository;
import com.sentinelbet.app.data.repository.SubscriptionRepository;
import com.sentinelbet.app.di.AppModule_ProvideApiFootballRepositoryFactory;
import com.sentinelbet.app.di.AppModule_ProvideBetDaoFactory;
import com.sentinelbet.app.di.AppModule_ProvideDatabaseFactory;
import com.sentinelbet.app.di.AppModule_ProvideOkHttpClientFactory;
import com.sentinelbet.app.di.AppModule_ProvideSettingsDaoFactory;
import com.sentinelbet.app.di.SupabaseModule_ProvideSupabaseClientFactory;
import com.sentinelbet.app.domain.usecases.ComputeAnalysisUseCase;
import com.sentinelbet.app.viewmodel.AnalysisViewModel;
import com.sentinelbet.app.viewmodel.AnalysisViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.AuthViewModel;
import com.sentinelbet.app.viewmodel.AuthViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.BankrollViewModel;
import com.sentinelbet.app.viewmodel.BankrollViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.DashboardViewModel;
import com.sentinelbet.app.viewmodel.DashboardViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.FixturesViewModel;
import com.sentinelbet.app.viewmodel.FixturesViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.HistoryViewModel;
import com.sentinelbet.app.viewmodel.HistoryViewModel_HiltModules;
import com.sentinelbet.app.viewmodel.ThemeViewModel;
import com.sentinelbet.app.viewmodel.ThemeViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.github.jan.supabase.SupabaseClient;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class DaggerSentinelBetApp_HiltComponents_SingletonC {
  private DaggerSentinelBetApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SentinelBetApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SentinelBetApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SentinelBetApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SentinelBetApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SentinelBetApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SentinelBetApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SentinelBetApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SentinelBetApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SentinelBetApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SentinelBetApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SentinelBetApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SentinelBetApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SentinelBetApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(7).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_AnalysisViewModel, AnalysisViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_BankrollViewModel, BankrollViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_FixturesViewModel, FixturesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_ThemeViewModel, ThemeViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sentinelbet_app_viewmodel_FixturesViewModel = "com.sentinelbet.app.viewmodel.FixturesViewModel";

      static String com_sentinelbet_app_viewmodel_ThemeViewModel = "com.sentinelbet.app.viewmodel.ThemeViewModel";

      static String com_sentinelbet_app_viewmodel_AuthViewModel = "com.sentinelbet.app.viewmodel.AuthViewModel";

      static String com_sentinelbet_app_viewmodel_BankrollViewModel = "com.sentinelbet.app.viewmodel.BankrollViewModel";

      static String com_sentinelbet_app_viewmodel_DashboardViewModel = "com.sentinelbet.app.viewmodel.DashboardViewModel";

      static String com_sentinelbet_app_viewmodel_HistoryViewModel = "com.sentinelbet.app.viewmodel.HistoryViewModel";

      static String com_sentinelbet_app_viewmodel_AnalysisViewModel = "com.sentinelbet.app.viewmodel.AnalysisViewModel";

      @KeepFieldType
      FixturesViewModel com_sentinelbet_app_viewmodel_FixturesViewModel2;

      @KeepFieldType
      ThemeViewModel com_sentinelbet_app_viewmodel_ThemeViewModel2;

      @KeepFieldType
      AuthViewModel com_sentinelbet_app_viewmodel_AuthViewModel2;

      @KeepFieldType
      BankrollViewModel com_sentinelbet_app_viewmodel_BankrollViewModel2;

      @KeepFieldType
      DashboardViewModel com_sentinelbet_app_viewmodel_DashboardViewModel2;

      @KeepFieldType
      HistoryViewModel com_sentinelbet_app_viewmodel_HistoryViewModel2;

      @KeepFieldType
      AnalysisViewModel com_sentinelbet_app_viewmodel_AnalysisViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SentinelBetApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AnalysisViewModel> analysisViewModelProvider;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<BankrollViewModel> bankrollViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<FixturesViewModel> fixturesViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<ThemeViewModel> themeViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.analysisViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.bankrollViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.fixturesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.themeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_AnalysisViewModel, ((Provider) analysisViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_BankrollViewModel, ((Provider) bankrollViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_FixturesViewModel, ((Provider) fixturesViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_sentinelbet_app_viewmodel_ThemeViewModel, ((Provider) themeViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sentinelbet_app_viewmodel_AuthViewModel = "com.sentinelbet.app.viewmodel.AuthViewModel";

      static String com_sentinelbet_app_viewmodel_AnalysisViewModel = "com.sentinelbet.app.viewmodel.AnalysisViewModel";

      static String com_sentinelbet_app_viewmodel_HistoryViewModel = "com.sentinelbet.app.viewmodel.HistoryViewModel";

      static String com_sentinelbet_app_viewmodel_BankrollViewModel = "com.sentinelbet.app.viewmodel.BankrollViewModel";

      static String com_sentinelbet_app_viewmodel_ThemeViewModel = "com.sentinelbet.app.viewmodel.ThemeViewModel";

      static String com_sentinelbet_app_viewmodel_FixturesViewModel = "com.sentinelbet.app.viewmodel.FixturesViewModel";

      static String com_sentinelbet_app_viewmodel_DashboardViewModel = "com.sentinelbet.app.viewmodel.DashboardViewModel";

      @KeepFieldType
      AuthViewModel com_sentinelbet_app_viewmodel_AuthViewModel2;

      @KeepFieldType
      AnalysisViewModel com_sentinelbet_app_viewmodel_AnalysisViewModel2;

      @KeepFieldType
      HistoryViewModel com_sentinelbet_app_viewmodel_HistoryViewModel2;

      @KeepFieldType
      BankrollViewModel com_sentinelbet_app_viewmodel_BankrollViewModel2;

      @KeepFieldType
      ThemeViewModel com_sentinelbet_app_viewmodel_ThemeViewModel2;

      @KeepFieldType
      FixturesViewModel com_sentinelbet_app_viewmodel_FixturesViewModel2;

      @KeepFieldType
      DashboardViewModel com_sentinelbet_app_viewmodel_DashboardViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sentinelbet.app.viewmodel.AnalysisViewModel 
          return (T) new AnalysisViewModel(singletonCImpl.computeAnalysisUseCaseProvider.get(), singletonCImpl.apiKeyRepositoryProvider.get());

          case 1: // com.sentinelbet.app.viewmodel.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.subscriptionRepositoryProvider.get());

          case 2: // com.sentinelbet.app.viewmodel.BankrollViewModel 
          return (T) new BankrollViewModel();

          case 3: // com.sentinelbet.app.viewmodel.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.betRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 4: // com.sentinelbet.app.viewmodel.FixturesViewModel 
          return (T) new FixturesViewModel(singletonCImpl.provideApiFootballRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 5: // com.sentinelbet.app.viewmodel.HistoryViewModel 
          return (T) new HistoryViewModel(singletonCImpl.betRepositoryProvider.get());

          case 6: // com.sentinelbet.app.viewmodel.ThemeViewModel 
          return (T) new ThemeViewModel(singletonCImpl.settingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SentinelBetApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SentinelBetApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends SentinelBetApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<ComputeAnalysisUseCase> computeAnalysisUseCaseProvider;

    private Provider<ApiKeyRepository> apiKeyRepositoryProvider;

    private Provider<SupabaseClient> provideSupabaseClientProvider;

    private Provider<SubscriptionRepository> subscriptionRepositoryProvider;

    private Provider<AuthRepository> authRepositoryProvider;

    private Provider<SentinelBetDatabase> provideDatabaseProvider;

    private Provider<BetRepository> betRepositoryProvider;

    private Provider<SettingsRepository> settingsRepositoryProvider;

    private Provider<ApiFootballRepository> provideApiFootballRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private BetDao betDao() {
      return AppModule_ProvideBetDaoFactory.provideBetDao(provideDatabaseProvider.get());
    }

    private SettingsDao settingsDao() {
      return AppModule_ProvideSettingsDaoFactory.provideSettingsDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 1));
      this.computeAnalysisUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<ComputeAnalysisUseCase>(singletonCImpl, 0));
      this.apiKeyRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ApiKeyRepository>(singletonCImpl, 2));
      this.provideSupabaseClientProvider = DoubleCheck.provider(new SwitchingProvider<SupabaseClient>(singletonCImpl, 4));
      this.subscriptionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SubscriptionRepository>(singletonCImpl, 5));
      this.authRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 3));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<SentinelBetDatabase>(singletonCImpl, 7));
      this.betRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BetRepository>(singletonCImpl, 6));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 8));
      this.provideApiFootballRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ApiFootballRepository>(singletonCImpl, 9));
    }

    @Override
    public void injectSentinelBetApp(SentinelBetApp sentinelBetApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sentinelbet.app.domain.usecases.ComputeAnalysisUseCase 
          return (T) new ComputeAnalysisUseCase(singletonCImpl.provideOkHttpClientProvider.get());

          case 1: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 2: // com.sentinelbet.app.data.repository.ApiKeyRepository 
          return (T) new ApiKeyRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.sentinelbet.app.data.repository.AuthRepository 
          return (T) new AuthRepository(singletonCImpl.provideSupabaseClientProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.subscriptionRepositoryProvider.get());

          case 4: // io.github.jan.supabase.SupabaseClient 
          return (T) SupabaseModule_ProvideSupabaseClientFactory.provideSupabaseClient();

          case 5: // com.sentinelbet.app.data.repository.SubscriptionRepository 
          return (T) new SubscriptionRepository(singletonCImpl.provideSupabaseClientProvider.get());

          case 6: // com.sentinelbet.app.data.repository.BetRepository 
          return (T) new BetRepository(singletonCImpl.betDao());

          case 7: // com.sentinelbet.app.data.db.SentinelBetDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.sentinelbet.app.data.repository.SettingsRepository 
          return (T) new SettingsRepository(singletonCImpl.settingsDao());

          case 9: // com.sentinelbet.app.data.repository.ApiFootballRepository 
          return (T) AppModule_ProvideApiFootballRepositoryFactory.provideApiFootballRepository(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
