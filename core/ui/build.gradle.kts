plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.drsapps.trackerforlegominifiguresseries.core.ui"
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
    // Deliberately no dependency on core:model/core:database/core:network/core:data - this module
    // is pure design system + generic Composables, with zero knowledge of domain models or data.
    val composeBom = platform("androidx.compose:compose-bom:2025.01.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // LocalLifecycleOwner (SeriesAndMinifiguresTopAppBar's double-tap-navigation guard)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    // ViewCompat/WindowInsetsCompat (SearchBar's keyboard-visibility listener)
    implementation("androidx.core:core-ktx:1.15.0")
}
