import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.postgresql

dependencies {
    implementation(project(":domain"))
    implementation(project(":db"))

    testImplementation(hikariCp)
    testImplementation(postgresql)
    testImplementation(kotestRunnerJunit)
    testImplementation(testFixtures(project(":domain")))

    testFixturesImplementation(project(":domain"))
}
