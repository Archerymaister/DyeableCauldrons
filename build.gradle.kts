plugins {
    java
    id("dev.projectshard.build.java-conventions") version "0.1.9"
    id("dev.projectshard.build.distribution") version "0.1.9"
    id("dev.projectshard.build.ci-cd") version "0.1.9"
}

group = "de.cubenation"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}