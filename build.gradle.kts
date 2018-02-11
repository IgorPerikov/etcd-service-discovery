group = "com.github.igorperikov.etcd"

subprojects.forEach({
    project: Project? ->
    run {
        project?.plugins?.apply("java")
        project?.configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_1_8
        }
        project?.repositories {
            mavenLocal()
            jcenter()
            mavenCentral()
        }

        setDependenyVersion(project, "jetcd", "0.0.1")
        setDependenyVersion(project, "guava", "23.0")
        setDependenyVersion(project, "testcontainers", "1.6.0")
        setDependenyVersion(project, "junit", "4.12")
    }
})

fun setDependenyVersion(project: Project?, key: String, value: String): Unit {
    project?.ext?.set(key, value)
}
