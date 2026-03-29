plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":common-lib"))
    implementation(libs.bundles.spring.web.stack)
    implementation(libs.bundles.spring.data.stack)
    implementation(libs.spring.cloud.eureka.client)
    implementation(libs.spring.kafka)              // Publishes payment events
    implementation(libs.spring.boot.starter.security)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    testImplementation(libs.bundles.testing)
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
