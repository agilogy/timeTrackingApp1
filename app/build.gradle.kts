import Dependencies.arrowCore
import Dependencies.kotestRunnerJunit
import Dependencies.kotlinXCoroutinesCore
import Dependencies.kotlinXSerializationJson


plugins {
    kotlin("jvm") version "1.8.10"
    `java-library`
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(8)) } }

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileKotlin.kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"

dependencies {

    implementation(arrowCore)
    implementation(kotlinXSerializationJson)
    implementation(kotlinXCoroutinesCore)
    testImplementation(kotestRunnerJunit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
