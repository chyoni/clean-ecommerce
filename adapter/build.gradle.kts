dependencies {
    implementation(project(":application"))
    implementation(project(":domain"))
    testImplementation(testFixtures(project(":domain")))

    implementation(rootProject.libs.aws.sdk.s3)
    implementation(rootProject.libs.spring.boot.validation)
    implementation(rootProject.libs.spring.boot.webmvc)

    testImplementation(rootProject.libs.testcontainers.junit)
    testImplementation(rootProject.libs.testcontainers.minio)
    testImplementation(rootProject.libs.spring.boot.webmvc.test)
}
