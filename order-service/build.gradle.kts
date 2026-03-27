plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":common-lib"))
    implementation(libs.bundles.spring.web.stack)
    implementation(libs.bundles.spring.data.stack)
    implementation(libs.spring.cloud.eureka.client)
    implementation(libs.spring.cloud.openfeign)    // Calls payment-service + inventory-service
    implementation(libs.spring.kafka)
    implementation(libs.resilience4j.spring.boot)  // Circuit breaker on downstream calls
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    testImplementation(libs.bundles.testing)
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
