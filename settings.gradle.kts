rootProject.name = "yaml-secrets-gradle-plugin"

include("examples:sample-project")

pluginManagement {
    repositories {
        mavenLocal()
        jcenter()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
}