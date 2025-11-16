import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kover)
    alias(libs.plugins.pluginPublish)
}

version = System.getenv("PLUGIN_VERSION") ?: "unspecified"

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(libs.bundles.compile)
    testImplementation(libs.bundles.test)
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    test {
        jvmArgs(
            "--add-opens", "java.base/java.util=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED"
        )
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

}

gradlePlugin {
    website = "https://github.com/PrzemyslawSwiderski/yaml-secrets-gradle-plugin"
    vcsUrl = "https://github.com/PrzemyslawSwiderski/yaml-secrets-gradle-plugin"
    plugins {
        create("yaml-secrets-gradle-plugin") {
            id = "com.pswidersk.yaml-secrets-plugin"
            implementationClass = "com.pswidersk.gradle.yamlsecrets.YamlSecretsPlugin"
            displayName = "Gradle plugin to load secret properties from Yaml files."
            description = "Gradle plugin to load secret properties from Yaml files."
            tags = listOf("yaml", "yml", "properties", "secrets")
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}