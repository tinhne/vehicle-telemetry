package com.vehicletelemetry.service;

import com.vehicletelemetry.data.repository.TelemetryRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class TelemetryForegroundService_MembersInjector implements MembersInjector<TelemetryForegroundService> {
  private final Provider<TelemetryRepository> repositoryProvider;

  public TelemetryForegroundService_MembersInjector(
      Provider<TelemetryRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<TelemetryForegroundService> create(
      Provider<TelemetryRepository> repositoryProvider) {
    return new TelemetryForegroundService_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(TelemetryForegroundService instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.vehicletelemetry.service.TelemetryForegroundService.repository")
  public static void injectRepository(TelemetryForegroundService instance,
      TelemetryRepository repository) {
    instance.repository = repository;
  }
}
