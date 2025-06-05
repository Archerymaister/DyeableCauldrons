plugins {
    java
    id("dev.projectshard.build.java-conventions") version "0.2.0-SNAPSHOT"
    id("dev.projectshard.build.distribution") version "0.2.0-SNAPSHOT"
    id("dev.projectshard.build.pipelines") version "0.2.0-SNAPSHOT"
}

group = "de.cubenation"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}