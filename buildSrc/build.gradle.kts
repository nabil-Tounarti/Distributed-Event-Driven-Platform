plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}
