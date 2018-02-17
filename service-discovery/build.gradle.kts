version = "0.0.1"

dependencies {
    compile("com.coreos", "jetcd-core", getDependencyVersion("jetcd"))
    compile("com.google.guava", "guava", getDependencyVersion("guava"))
    compile("org.springframework.cloud", "spring-cloud-commons", getDependencyVersion("spring-cloud-commons"))
    testCompile("org.testcontainers", "testcontainers", getDependencyVersion("testcontainers"))
    testCompile("junit", "junit", getDependencyVersion("junit"))
}

fun getDependencyVersion(key: String): String {
    return project.property(key).toString()
}
