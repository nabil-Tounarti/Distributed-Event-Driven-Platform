rootProject.name = "ecommerce-platform"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include (
    "api-gateway",
    "common-lib",
    "inventory-service",
    "notification-service",
    "order-service",
    "payment-service",
    "product-service",
    "user-service"
)
