package com.vehicletelemetry.presentation.dashboard;

import com.vehicletelemetry.domain.usecase.GetLiveTelemetryUseCase;
import com.vehicletelemetry.domain.usecase.ObserveWarningsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetLiveTelemetryUseCase> getLiveTelemetryProvider;

  private final Provider<ObserveWarningsUseCase> observeWarningsProvider;

  public DashboardViewModel_Factory(Provider<GetLiveTelemetryUseCase> getLiveTelemetryProvider,
      Provider<ObserveWarningsUseCase> observeWarningsProvider) {
    this.getLiveTelemetryProvider = getLiveTelemetryProvider;
    this.observeWarningsProvider = observeWarningsProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getLiveTelemetryProvider.get(), observeWarningsProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetLiveTelemetryUseCase> getLiveTelemetryProvider,
      Provider<ObserveWarningsUseCase> observeWarningsProvider) {
    return new DashboardViewModel_Factory(getLiveTelemetryProvider, observeWarningsProvider);
  }

  public static DashboardViewModel newInstance(GetLiveTelemetryUseCase getLiveTelemetry,
      ObserveWarningsUseCase observeWarnings) {
    return new DashboardViewModel(getLiveTelemetry, observeWarnings);
  }
}
