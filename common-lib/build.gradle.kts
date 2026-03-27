plugins {
	id("java-conventions")
	`java-library`
}

dependencies {
	api(libs.spring.boot.starter.validation)
	api(libs.lombok)
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)
	
	testImplementation(libs.spring.boot.starter.test)
}
