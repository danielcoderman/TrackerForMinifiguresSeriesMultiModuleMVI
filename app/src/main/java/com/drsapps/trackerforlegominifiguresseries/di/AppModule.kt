package com.drsapps.trackerforlegominifiguresseries.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.drsapps.trackerforlegominifiguresseries.data.local.TrackerForLegoMinifiguresSeriesDatabase
import com.drsapps.trackerforlegominifiguresseries.data.remote.SeriesMinifigCatalogApi
import com.drsapps.trackerforlegominifiguresseries.data.repository.AppStateRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.AppUsageRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.MinifigureInventoryItemRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.MinifigureRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.SeriesRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.SyncRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.repository.UserPreferencesRepositoryImpl
import com.drsapps.trackerforlegominifiguresseries.data.worker.DataSyncScheduler
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppState
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppStateSerializer
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppUsage
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppUsageSerializer
import com.drsapps.trackerforlegominifiguresseries.domain.model.UserPreferences
import com.drsapps.trackerforlegominifiguresseries.domain.model.UserPreferencesSerializer
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppStateRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureInventoryItemRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.MinifigureRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SeriesRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SyncRepository
import com.drsapps.trackerforlegominifiguresseries.domain.repository.UserPreferencesRepository
import com.drsapps.trackerforlegominifiguresseries.domain.use_case.SyncDataUseCase
import com.drsapps.trackerforlegominifiguresseries.presentation.MainViewModelReviewDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides // causes hilt to create an instance
    fun provideAppDatabase(app: Application): TrackerForLegoMinifiguresSeriesDatabase { // [1]
        return Room.databaseBuilder(
            app, // an Application is a subclass of Context
            TrackerForLegoMinifiguresSeriesDatabase::class.java,
            TrackerForLegoMinifiguresSeriesDatabase.DATABASE_NAME
        )
            .createFromAsset("database/trackerForLegoMinifiguresSeries.db")
            .addMigrations(
                TrackerForLegoMinifiguresSeriesDatabase.migration1To2,
                TrackerForLegoMinifiguresSeriesDatabase.migration2To3,
                TrackerForLegoMinifiguresSeriesDatabase.migration1To4,
                TrackerForLegoMinifiguresSeriesDatabase.migration2to4,
                TrackerForLegoMinifiguresSeriesDatabase.migration3to4
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideSeriesRepository(db: TrackerForLegoMinifiguresSeriesDatabase): SeriesRepository {
        return SeriesRepositoryImpl(db.getSeriesDao())
    }

    @Singleton
    @Provides
    fun provideMinifigureRepository(db: TrackerForLegoMinifiguresSeriesDatabase): MinifigureRepository {
        return MinifigureRepositoryImpl(db.getMinifigureDao())
    }

    @Provides
    fun provideMinifigureInventoryItemRepository(db: TrackerForLegoMinifiguresSeriesDatabase): MinifigureInventoryItemRepository {
        return MinifigureInventoryItemRepositoryImpl(db.getMinifigureInventoryItemDao())
    }

    @Singleton
    @Provides
    fun provideAppStateDataStore(
        @ApplicationContext appContext: Context,
        serializer: AppStateSerializer
    ): DataStore<AppState> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = {
                appContext.dataStoreFile("app-state.json")
            }
        )
    }

    // Note: There shouldn't be an issue with an instance of AppStateRepository being re-created
    // when needed.
    @Provides
    fun provideAppStateRepository(appStateDataStore: DataStore<AppState>): AppStateRepository {
        return AppStateRepositoryImpl(appStateDataStore)
    }

    @Singleton
    @Provides
    fun provideAppUsageDataStore(
        @ApplicationContext appContext: Context,
        serializer: AppUsageSerializer
    ): DataStore<AppUsage> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = {
                appContext.dataStoreFile("app-usage.json")
            }
        )
    }

    @Provides
    fun provideAppUsageRepository(appUsageDataStore: DataStore<AppUsage>): AppUsageRepository {
        return AppUsageRepositoryImpl(appUsageDataStore)
    }

    @Singleton // causes Hilt to provide the same instance for the lifetime of the app (So only use if absolutely necessary or if the instance is expensive to create)
    @Provides
    fun provideUserPreferencesDataStore(
        @ApplicationContext appContext: Context,
        serializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = {
                appContext.dataStoreFile("user-preferences.json")
            }
        )
    }

    @Singleton
    @Provides
    fun provideUserPreferencesRepository(userPrefDataStore: DataStore<UserPreferences>): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(userPrefDataStore)
    }

    @Provides
    fun provideMainViewModelReviewDelegate(): MainViewModelReviewDelegate {
        return MainViewModelReviewDelegate()
    }

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // Helpful for API changes
    }

    @Singleton
    @Provides
    fun provideSeriesMinifigCatalogApi(json: Json): SeriesMinifigCatalogApi {
        return Retrofit.Builder()
            .baseUrl(SeriesMinifigCatalogApi.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SeriesMinifigCatalogApi::class.java)
    }

    // The SyncRepository is a singleton so that the mutex can work as expected.
    @Singleton
    @Provides
    fun provideSyncRepository(
        api: SeriesMinifigCatalogApi,
        db: TrackerForLegoMinifiguresSeriesDatabase
    ): SyncRepository {
        return SyncRepositoryImpl(api, db.getUpsertDao())
    }

    @Provides
    fun provideSyncDataUseCase(
        syncRepo: SyncRepository,
        appStateRepo: AppStateRepository
    ): SyncDataUseCase {
        return SyncDataUseCase(syncRepo, appStateRepo)
    }

    @Provides
    fun provideDataSyncScheduler(
        @ApplicationContext context: Context
    ): DataSyncScheduler {
        return DataSyncScheduler(context)
    }

}

// Note:
// [1] The application binding is available without qualifiers
//
// A binding's scope must match the scope of the component where it is installed