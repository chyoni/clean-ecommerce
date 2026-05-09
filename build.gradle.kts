import com.diffplug.gradle.spotless.SpotlessExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.spotless)
}

allprojects {
    group = "cwchoiit"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${rootProject.libs.versions.spring.boot.get()}")
        }
    }

    val mockitoAgent = configurations.create("mockitoAgent") {
        isTransitive = false
    }

    dependencies {
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)

        testCompileOnly(rootProject.libs.lombok)
        testAnnotationProcessor(rootProject.libs.lombok)
        testImplementation(rootProject.libs.junit.platform.launcher)
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.spring.boot.test)

        mockitoAgent(rootProject.libs.mockito.core)
    }

    // Spotless lint automatically applied in compile time
    if (System.getProperty("idea.active") == "true") {
        tasks.named("processResources") {
            dependsOn("spotlessApply")
        }
    }

    configure<SpotlessExtension> {
        java {
            target("**/*.java")
            targetExclude("**/generated/**/*.java")
            googleJavaFormat().aosp().skipJavadocFormatting()
            removeUnusedImports()
            importOrder()
            endWithNewline()
            trimTrailingWhitespace()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs("-javaagent:${mockitoAgent.singleFile}")
    }
}
