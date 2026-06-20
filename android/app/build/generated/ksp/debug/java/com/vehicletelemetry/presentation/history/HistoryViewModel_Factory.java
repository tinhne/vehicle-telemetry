package com.vehicletelemetry.presentation.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<ObserveWarningsUseCase> observeWarningsProvider;

  public HistoryViewModel_Factory(Provider<ObserveWarningsUseCase> observeWarningsProvider) {
    this.observeWarningsProvider = observeWarningsProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(observeWarningsProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<ObserveWarningsUseCase> observeWarningsProvider) {
    return new HistoryViewModel_Factory(observeWarningsProvider);
  }

  public static HistoryViewModel newInstance(ObserveWarningsUseCase observeWarnings) {
    return new HistoryViewModel(observeWarnings);
  }
}
