repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

plugins {
    kotlin("jvm") version "1.8.21"
}
