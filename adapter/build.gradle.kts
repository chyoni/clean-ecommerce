dependencies {
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(rootProject.libs.aws.sdk.s3)
    implementation(rootProject.libs.spring.boot.validation)

    testImplementation(rootProject.libs.testcontainers.junit)
    testImplementation(rootProject.libs.testcontainers.minio)
}
