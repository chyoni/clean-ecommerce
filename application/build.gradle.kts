dependencies {
    implementation(project(":domain"))
    testImplementation(testFixtures(project(":domain")))

    implementation(libs.spring.boot.data.jpa)
    implementation(libs.spring.boot.validation)

    testImplementation(libs.spring.boot.data.jpa.test)
}
