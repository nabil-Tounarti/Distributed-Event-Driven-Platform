plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":common-lib"))
    implementation(libs.bundles.spring.web.stack)
    implementation(libs.bundles.spring.data.stack)
    implementation(libs.nimbus)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.cloud.eureka.client)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
