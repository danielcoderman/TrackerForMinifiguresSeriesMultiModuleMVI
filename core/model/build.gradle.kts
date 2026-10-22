plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        // Without an actual JDK 17 installed, jvmTarget alone only fixes the output bytecode
        // version - the compiler (JDK 21) would still let source reference JDK 18-21-only stdlib
        // APIs. This restricts the API surface to JDK 17 too, so compiling with a newer JDK can't
        // silently produce a class that references a symbol JDK 17 doesn't actually have.
        freeCompilerArgs.add("-Xjdk-release=17")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// No dependencies: every class here is a plain Kotlin data class with zero framework imports.
// Series/Minifigure/MinifigureInventoryItem (the Room entities) moved to core/database instead -
// KSP can't resolve a Room entity declared in a plain kotlin("jvm") module from an Android
// library module's annotation processor (a known cross-platform-type KSP limitation), and
// conceptually they belong next to the @Dao/@Database that processes them anyway.
