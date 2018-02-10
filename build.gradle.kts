group = "com.github.igorperikov.etcd"
version = "0.0.1"

plugins {
   java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testCompile("junit:junit:4.12")
}

repositories {
    jcenter()
    mavenLocal()
}
