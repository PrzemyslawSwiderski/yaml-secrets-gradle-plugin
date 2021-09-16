rootProject.name = "yaml-secrets-gradle-plugin"

include("examples:sample-project",
        "examples:sample-groovy-dsl-project",
        "examples:sample-python-project",
        "examples:sample-multi-module-project",
        "examples:sample-multi-module-project:nested-project")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}