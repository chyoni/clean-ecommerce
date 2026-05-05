plugins {
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":adapter"))
    implementation(project(":application"))
    implementation(project(":domain"))

    implementation(libs.spring.boot.webmvc)
    implementation(libs.spring.boot.data.jpa)

    testImplementation(libs.spring.boot.test)
    testImplementation(libs.spring.boot.webmvc.test)
    testImplementation(libs.spring.boot.data.jpa.test)

    runtimeOnly(libs.h2)
    runtimeOnly(libs.mysql.connector)
}
