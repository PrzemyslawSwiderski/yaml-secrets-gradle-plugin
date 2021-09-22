rootProject.name = "yaml-secrets-gradle-plugin"

include(
    "examples:sample-project",
    "examples:sample-groovy-dsl-project",
    "examples:sample-python-project",
    "examples:sample-multi-module-project",
    "examples:sample-multi-module-project:nested-project"
)

pluginManagement {

    val yamlSecretsPluginVersionForExamples: String by settings
    plugins {
        id("com.pswidersk.yaml-secrets-plugin") version yamlSecretsPluginVersionForExamples
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}