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

dependencies {
    // Only the annotation classes (@Entity, @ColumnInfo, @PrimaryKey, @ForeignKey, @Index) -
    // the Room runtime and the KSP compiler stay with whichever module owns the @Database
    // (core/database, once that's extracted). See the note on Series/Minifigure/
    // MinifigureInventoryItem for why a "pure model" module needs this at all.
    implementation("androidx.room:room-common:2.6.1")
}
