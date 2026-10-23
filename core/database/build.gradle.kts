plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.drsapps.trackerforlegominifiguresseries.core.database"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    sourceSets {
        // Adds exported schema location as test app assets, so MigrationTestHelper can create
        // past-version databases from them.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
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
    // api: the DAOs return MinifigureHiddenState/MinifigureWithSeriesName/SeriesHiddenState/
    // SeriesIdAndName (core:model types) directly in their own method signatures.
    api(project(":core:model"))

    val roomVersion = "2.6.1"
    val pagingVersion = "3.3.5"

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // Paging 3 Integration
    implementation("androidx.room:room-paging:$roomVersion")
    // api: the DAOs return PagingSource<Int, T> directly - Paging isn't an incidental transport
    // detail here, it's the mechanism Compose consumes all the way up the stack.
    api("androidx.paging:paging-runtime-ktx:$pagingVersion")

    // Test
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    // The test APK needs AndroidJUnitRunner (named in testInstrumentationRunner above) on its own
    // classpath. :app gets this for free transitively via Espresso/Compose UI-test deps it already
    // has - this module has neither, so it needs the explicit dependency.
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
