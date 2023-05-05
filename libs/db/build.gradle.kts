import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.postgresql

dependencies {
    testImplementation(postgresql)
    implementation(hikariCp)
    testImplementation(kotestRunnerJunit)
}
