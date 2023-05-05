import Dependencies.ktorClient
import Dependencies.ktorServerCore
import Dependencies.ktorTest

dependencies {
    implementation(ktorServerCore)
    implementation(project(":json"))
    implementation(project(":domain"))
    testImplementation(ktorClient)
    testImplementation(ktorTest)
    testImplementation(testFixtures(project(":domain")))
}
