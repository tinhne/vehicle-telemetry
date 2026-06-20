package com.vehicletelemetry.data.remote.websocket;

import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class TelemetryWebSocketClient_Factory implements Factory<TelemetryWebSocketClient> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Gson> gsonProvider;

  public TelemetryWebSocketClient_Factory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Gson> gsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public TelemetryWebSocketClient get() {
    return newInstance(okHttpClientProvider.get(), gsonProvider.get());
  }

  public static TelemetryWebSocketClient_Factory create(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Gson> gsonProvider) {
    return new TelemetryWebSocketClient_Factory(okHttpClientProvider, gsonProvider);
  }

  public static TelemetryWebSocketClient newInstance(OkHttpClient okHttpClient, Gson gson) {
    return new TelemetryWebSocketClient(okHttpClient, gson);
  }
}
