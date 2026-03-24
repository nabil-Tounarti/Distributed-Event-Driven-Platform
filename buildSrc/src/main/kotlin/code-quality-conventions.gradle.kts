plugins {
    id("com.diffplug.spotless")
}

spotless {
    java {
        googleJavaFormat("1.19.1")
        removeUnusedImports()
        trimTrailingWhitespace()
    }
}