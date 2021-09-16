plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.5.21"
    id("com.gradle.plugin-publish") version "0.11.0"
    id("net.researchgate.release") version "2.8.1"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.5.21"))
    implementation(kotlin("reflect", "1.5.21"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
    "afterReleaseBuild"{
        dependsOn("publish", "publishPlugins")
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
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