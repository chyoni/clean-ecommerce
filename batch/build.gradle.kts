dependencies {
    runtimeOnly(libs.h2)
    runtimeOnly(libs.mysql.connector)

    implementation(libs.spring.boot.batch.jdbc)

    testImplementation(libs.spring.boot.batch.jdbc.test)
}