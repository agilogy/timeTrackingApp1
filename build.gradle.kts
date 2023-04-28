import Dependencies.arrowCore
import Dependencies.arrowFxCoroutines
import Dependencies.kotestRunnerJunit
import Dependencies.kotlinXCoroutinesCore

plugins {
    (kotlin("jvm") version "1.8.21") apply false
}

// Common traits to all the projects in the build. See
// https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:defining_common_behavior
// We can't use type-safe accessors when configuring subprojects within the root build script. See
// https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:multi_project_builds and
// https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin_cross_project_configuration
allprojects {
    group = "com.agilogy"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java-test-fixtures")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            allWarningsAsErrors = true
            freeCompilerArgs = listOf("-progressive", "-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        "implementation"(arrowCore)
        "implementation"(arrowFxCoroutines)
        "implementation"(kotlinXCoroutinesCore)
        "testImplementation"(kotestRunnerJunit)
    }

}