package com.vehicletelemetry.core.di;

import com.vehicletelemetry.data.local.db.TelemetryDatabase;
import com.vehicletelemetry.data.local.db.dao.TelemetryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideDaoFactory implements Factory<TelemetryDao> {
  private final Provider<TelemetryDatabase> dbProvider;

  public DatabaseModule_ProvideDaoFactory(Provider<TelemetryDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TelemetryDao get() {
    return provideDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDaoFactory create(Provider<TelemetryDatabase> dbProvider) {
    return new DatabaseModule_ProvideDaoFactory(dbProvider);
  }

  public static TelemetryDao provideDao(TelemetryDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDao(db));
  }
}
