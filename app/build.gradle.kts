import Dependencies.arrowCore
import Dependencies.arrowFxCoroutines
import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.kotlinXCoroutinesCore
import Dependencies.kotlinXSerializationJson
import Dependencies.postgresql

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(8)) } }

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("com.agilogy.timetracking.driveradapters.console.ConsoleAppKt")
}

dependencies {

    implementation(arrowCore)
    implementation(arrowFxCoroutines)
    implementation(kotlinXSerializationJson)
    implementation(kotlinXCoroutinesCore)
    implementation(postgresql)
    implementation(hikariCp)
    testImplementation(kotestRunnerJunit)
}

tasks.test {
    useJUnitPlatform()
}
