plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.drsapps.trackerforlegominifiguresseries.core.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // api: SeriesHiddenState/MinifigureHiddenState/MinifigureWithSeriesName/SeriesIdAndName (and
    // Layout, via UserPreferences.layout) appear directly in these repository interfaces' own
    // signatures.
    api(project(":core:model"))
    // api: SeriesRepository/MinifigureRepository/etc. return Series/Minifigure/
    // MinifigureInventoryItem (core:database types) directly.
    api(project(":core:database"))
    // implementation: network DTOs/SeriesMinifigCatalogApi are only used inside
    // SyncRepositoryImpl - SyncRepository's own interface exposes only SyncState, so nothing here
    // needs core:network visible to consumers.
    implementation(project(":core:network"))

    // AppStateSerializer/AppUsageSerializer/UserPreferencesSerializer use @Inject/@Singleton
    // (JSR-330), but this module applies no Hilt/Dagger compiler itself - Dagger generates their
    // _Factory classes later, during :app's own Hilt build, once it can see these classes through
    // the project dependency. This is just the annotation types so the file compiles here.
    implementation("javax.inject:javax.inject:1")

    implementation("androidx.datastore:datastore:1.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // api: AppState itself, and AppStateRepository's own methods (updateLastReviewRequestTimestamp,
    // updateLastFetchedTimestamp), expose kotlinx.datetime.Instant directly.
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    // api: SeriesRepository/MinifigureRepository return PagingSource<Int, T> directly too.
    api("androidx.paging:paging-runtime-ktx:3.3.5")
}
