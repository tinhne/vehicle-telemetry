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
public final class GetLiveTelemetryUseCase_Factory implements Factory<GetLiveTelemetryUseCase> {
  private final Provider<TelemetryRepository> repoProvider;

  public GetLiveTelemetryUseCase_Factory(Provider<TelemetryRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetLiveTelemetryUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetLiveTelemetryUseCase_Factory create(Provider<TelemetryRepository> repoProvider) {
    return new GetLiveTelemetryUseCase_Factory(repoProvider);
  }

  public static GetLiveTelemetryUseCase newInstance(TelemetryRepository repo) {
    return new GetLiveTelemetryUseCase(repo);
  }
}
