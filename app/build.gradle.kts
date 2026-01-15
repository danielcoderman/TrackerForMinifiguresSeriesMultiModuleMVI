import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
// Initialize a new Properties() object called keystoreProperties
val keystoreProperties = Properties()
// Load the keystore.properties file into the keystoreProperties object.
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    namespace = "com.drsapps.trackerforlegominifiguresseries"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.drsapps.trackerforlegominifiguresseries"
        minSdk = 26
        targetSdk = 35
        versionCode = 19
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            proguardFiles(
                // The Android Gradle plugin generates proguard-android-optimize.txt, which includes
                // rules that are useful to most Android projects and enables @Keep* annotations.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // List additional ProGuard rules for the given build type here. By default,
                // Android Studio creates and includes an empty rules file for you (located
                // at the root directory of each module).
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        // A temporary/test build variant
        create("tempa") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            isShrinkResources = false
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val navVersion = "2.8.6"
    val roomVersion = "2.6.1"
    val pagingVersion = "3.3.5"
    val lifecycleVersion = "2.8.7"
    val workVersion = "2.10.3"
    val composeBom = platform("androidx.compose:compose-bom:2025.01.01")

    implementation("androidx.core:core-ktx:1.15.0")

    // androidx.lifecycle (https://developer.android.com/jetpack/androidx/releases/lifecycle)
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Lifecycle utilities for Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    // Annotation processor alternative
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    implementation("androidx.activity:activity-compose:1.10.0")

    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material3.adaptive:adaptive:1.1.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Dagger-Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    ksp("com.google.dagger:hilt-android-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // Paging 3 Integration
    implementation("androidx.room:room-paging:$roomVersion")

    // Coil (https://coil-kt.github.io/coil/changelog/)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Paging (https://developer.android.com/jetpack/androidx/releases/paging)
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    // Jetpack Compose integration
    implementation("androidx.paging:paging-compose:$pagingVersion")

    // DataStore (https://developer.android.com/jetpack/androidx/releases/datastore)
    implementation("androidx.datastore:datastore:1.1.2")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Play In-App Review Library
    implementation("com.google.android.play:review:2.0.2")
    // For Kotlin users also import the Kotlin extensions library for Play In-App Review:
    implementation("com.google.android.play:review-ktx:2.0.2")

    // I'm using this to get access to the automatically serializable Instant type
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // Splash Screen API (Backwards compatible)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // WorkManager dependency
    implementation("androidx.work:work-runtime:$workVersion")

    // Window Manager
    implementation("androidx.window:window:1.4.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation(composeBom)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Room Migration Test
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    // This allows suspend functions to be invoked in tests with the help of "runTest"
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}

// This enables the experimental strong skipping mode which is enabled by default in Kotlin 2.0.20.
// With strong skipping enabled, unstable parameters are usually skipped which improves performance
// by not having a lot of unnecessary recompositions.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
    )
}