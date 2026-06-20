package com.vehicletelemetry.data.repository;

import com.vehicletelemetry.data.remote.websocket.TelemetryWebSocketClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class TelemetryRepositoryImpl_Factory implements Factory<TelemetryRepositoryImpl> {
  private final Provider<TelemetryWebSocketClient> wsClientProvider;

  public TelemetryRepositoryImpl_Factory(Provider<TelemetryWebSocketClient> wsClientProvider) {
    this.wsClientProvider = wsClientProvider;
  }

  @Override
  public TelemetryRepositoryImpl get() {
    return newInstance(wsClientProvider.get());
  }

  public static TelemetryRepositoryImpl_Factory create(
      Provider<TelemetryWebSocketClient> wsClientProvider) {
    return new TelemetryRepositoryImpl_Factory(wsClientProvider);
  }

  public static TelemetryRepositoryImpl newInstance(TelemetryWebSocketClient wsClient) {
    return new TelemetryRepositoryImpl(wsClient);
  }
}
