package com.pswidersk.gradle.yamlsecrets

import org.gradle.api.Plugin
import org.gradle.api.Project

class YamlSecretsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(YAML_SECRETS_PLUGIN_EXTENSION_NAME, YamlSecretsResolver::class.java)
        val dirsToSearchForSecrets = generateSequence(project) { it.parent }.sorted().map { p ->
            p.projectDir
        }
        loadSecretsByDirs(project.secrets, dirsToSearchForSecrets)
        addSecretFilesToGitIgnore(project)
    }

    private fun addSecretFilesToGitIgnore(target: Project) {
        val gitIgnoreFile = target.rootDir.resolve(".gitignore")
        if (gitIgnoreFile.exists()) {
            val yamSecretsHeader = "### Yaml Secrets files ###"
            with(gitIgnoreFile) {
                if (readLines().none { it.contains(yamSecretsHeader) }) {
                    appendText("${System.lineSeparator()}$yamSecretsHeader")
                    SECRET_EXTENSIONS.forEach {
                        appendText("${System.lineSeparator()}$HIDDEN_PREFIX*$it")
                    }
                }
            }
        }
    }

}
