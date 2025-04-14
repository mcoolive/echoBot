description = "A bot that listens for incoming messages and generates programmable, rule-based responses"

plugins {
    java
    application
    id("org.springframework.boot") version "2.7.18" // Provides Spring Boot support (BOM, bootJar)
}

group = "fr.mcoolive"
version = "2.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.18"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.13.5"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.jayway.jsonpath:json-path")

    //implementation("ch.qos.logback:logback-classic:1.5.17")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testCompileOnly("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.mockito:mockito-core")
}
