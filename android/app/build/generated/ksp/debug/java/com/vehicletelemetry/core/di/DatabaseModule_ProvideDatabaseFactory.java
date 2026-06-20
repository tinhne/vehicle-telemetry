package com.vehicletelemetry.core.di;

import android.content.Context;
import com.vehicletelemetry.data.local.db.TelemetryDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideDatabaseFactory implements Factory<TelemetryDatabase> {
  private final Provider<Context> ctxProvider;

  public DatabaseModule_ProvideDatabaseFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public TelemetryDatabase get() {
    return provideDatabase(ctxProvider.get());
  }

  public static DatabaseModule_ProvideDatabaseFactory create(Provider<Context> ctxProvider) {
    return new DatabaseModule_ProvideDatabaseFactory(ctxProvider);
  }

  public static TelemetryDatabase provideDatabase(Context ctx) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDatabase(ctx));
  }
}
