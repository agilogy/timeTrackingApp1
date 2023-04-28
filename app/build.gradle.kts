import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.ktorServerNetty
import Dependencies.postgresql
import Dependencies.suspendApp
import Dependencies.suspendAppKtor

plugins {
    application
}

application {
    mainClass.set("com.agilogy.timetracking.ConsoleAppKt")
}

dependencies {
    implementation(postgresql)
    implementation(ktorServerNetty)
    implementation(project(":db"))
    implementation(project(":domain"))
    implementation(project(":console"))
    implementation(project(":postgresdb"))
    implementation(project(":httpapi"))
    implementation(suspendApp)
    implementation(suspendAppKtor)
    implementation(hikariCp)
    testImplementation(kotestRunnerJunit)
}
