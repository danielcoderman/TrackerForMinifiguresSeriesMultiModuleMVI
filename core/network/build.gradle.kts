plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xjdk-release=17")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// No dependency on core:model or core:database - the DTOs only model the wire format. Mapping
// a DTO to a Room "Partial" upsert type is a cross-cutting concern that belongs to whichever
// module composes both network and database (core/data, once extracted) - see the note next to
// SyncRepositoryImpl.toPartialSeries() et al. in :app for where that mapping lives for now.
dependencies {
    // api, not implementation: SeriesMinifigCatalogApi's methods return retrofit2.Response<T>
    // directly, so that type is part of this module's own public surface - any consumer calling
    // the API needs retrofit2.Response on its classpath too, not just this module's internals.
    api("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
