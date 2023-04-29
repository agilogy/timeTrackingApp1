rootProject.name = "listings"

plugins {
    id("com.gradle.enterprise") version ("3.9")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}



rootProject.name = "timeTrackingApp"
include("app")

val components = File("${rootDir}/components/").listFiles()!!.filter { it.isDirectory }.map { it.name }
components.forEach { configureProject(it, "components") }

val libs = File("${rootDir}/libs/").listFiles()!!.filter { it.isDirectory }.map { it.name }
libs.forEach { configureProject(it, "libs") }

fun configureProject(name: String, path: String) {
    include(":$name")
    project(":$name").projectDir = File("$path/$name")
}

