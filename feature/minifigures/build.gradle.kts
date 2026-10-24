plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.drsapps.trackerforlegominifiguresseries.feature.minifigures"
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

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    // core:model and core:database are also exposed transitively through core:data's own api(...)
    // dependencies, but this module's own source directly imports types from both (MinifigureHiddenState/
    // SeriesIdAndName/MinifigureWithSeriesName from core:model; Minifigure/MinifigureInventoryItem from
    // core:database) - so both are declared here too, rather than relying on core:data's choice to
    // re-expose them.
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    val composeBom = platform("androidx.compose:compose-bom:2025.01.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material3.adaptive:adaptive:1.1.0")
    implementation("androidx.compose.runtime:runtime")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.window:window:1.4.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Dagger-Hilt, applied locally - same reasoning as feature:series (@HiltViewModel's
    // aggregating processor must run in the module where the class is compiled).
    implementation("com.google.dagger:hilt-android:2.49")
    ksp("com.google.dagger:hilt-android-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Paging 3 Compose integration (MinifiguresSearchScreen's collectAsLazyPagingItems)
    implementation("androidx.paging:paging-compose:3.3.5")

    // Coil (MinifigureImageCard/MinifigureCard image loading)
    implementation("io.coil-kt:coil-compose:2.6.0")
}
