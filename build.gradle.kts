import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.3.72"
    id("com.gradle.plugin-publish") version "0.11.0"
    id("net.researchgate.release") version "2.8.1"
}

repositories {
    mavenLocal()
    jcenter()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
    "afterReleaseBuild"{
        dependsOn("publish", "publishPlugins")
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
gradlePlugin {
    plugins {
        create("yaml-secrets-gradle-plugin") {
            id = "com.pswidersk.yaml-secrets-plugin"
            implementationClass = "com.pswidersk.gradle.yamlsecrets.YamlSecretsPlugin"
            displayName = "Gradle plugin to load secret properties from Yaml files. https://github.com/PrzemyslawSwiderski/yaml-secrets-gradle-plugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/PrzemyslawSwiderski/yaml-secrets-gradle-plugin"
    vcsUrl = "https://github.com/PrzemyslawSwiderski/yaml-secrets-gradle-plugin"
    description = "Gradle plugin to load secret properties from Yaml files."
    tags = listOf("yaml", "yml", "properties", "secrets")
}

publishing {
    repositories {
        mavenLocal()
    }
}