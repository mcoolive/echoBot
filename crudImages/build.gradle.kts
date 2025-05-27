description = "A CRUD HTTP API to manage images"

plugins {
    java
    application
    id("org.springframework.boot") version "3.4.5" // Provides Spring Boot support (BOM, bootJar)
    id("com.gorylenko.gradle-git-properties") version "2.5.0" // Produces a git.properties for spring boot actuator
}

group = "fr.mcoolive"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

springBoot {
    buildInfo() // Produces build-info.properties for spring boot actuator
}

repositories {
    mavenCentral()
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-logging")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("com.github.ben-manes.caffeine:caffeine")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-core")
}

