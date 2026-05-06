plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.spring.boot.data.jpa)
    implementation(libs.spring.boot.validation)
    implementation(libs.jackson.databind)
}