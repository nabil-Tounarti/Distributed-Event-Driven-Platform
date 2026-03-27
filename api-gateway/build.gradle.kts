plugins {
	id("spring-conventions")
}

dependencies {
	implementation(project(":common-lib"))
	implementation(libs.spring.cloud.gateway)
	implementation(libs.spring.cloud.eureka.client)
	implementation(libs.resilience4j.spring.boot)
	implementation(libs.spring.boot.starter.actuator)
}
