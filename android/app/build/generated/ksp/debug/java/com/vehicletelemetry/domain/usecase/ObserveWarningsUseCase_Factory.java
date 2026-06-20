package com.vehicletelemetry.domain.usecase;

import com.vehicletelemetry.data.repository.TelemetryRepository;
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
public final class ObserveWarningsUseCase_Factory implements Factory<ObserveWarningsUseCase> {
  private final Provider<TelemetryRepository> repoProvider;

  public ObserveWarningsUseCase_Factory(Provider<TelemetryRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ObserveWarningsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static ObserveWarningsUseCase_Factory create(Provider<TelemetryRepository> repoProvider) {
    return new ObserveWarningsUseCase_Factory(repoProvider);
  }

  public static ObserveWarningsUseCase newInstance(TelemetryRepository repo) {
    return new ObserveWarningsUseCase(repo);
  }
}
