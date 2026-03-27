plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":common-lib"))
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.cloud.eureka.client)
    implementation(libs.spring.kafka)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.kafka)
    testImplementation("com.h2database:h2")
}
